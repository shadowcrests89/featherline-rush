package com.Pupcevi3.Xekqes.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Pupcevi3.Xekqes.core.config.GameConfig
import com.Pupcevi3.Xekqes.domain.model.EndReason
import com.Pupcevi3.Xekqes.domain.model.GameSettings
import com.Pupcevi3.Xekqes.domain.model.Lane
import com.Pupcevi3.Xekqes.domain.model.LaneKind
import com.Pupcevi3.Xekqes.domain.model.RouteMath
import com.Pupcevi3.Xekqes.domain.model.RunResult
import com.Pupcevi3.Xekqes.domain.repository.DrillRepository
import com.Pupcevi3.Xekqes.domain.repository.SettingsRepository
import com.Pupcevi3.Xekqes.domain.usecase.GenerateRouteUseCase
import com.Pupcevi3.Xekqes.domain.usecase.SubmitRunUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

sealed interface GameEvent {
    data class Finished(val result: RunResult, val rank: Int) : GameEvent
}

class GameViewModel(
    private val settingsRepository: SettingsRepository,
    private val drillRepository: DrillRepository,
    private val generateRoute: GenerateRouteUseCase,
    private val submitRun: SubmitRunUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var loop: Job? = null
    private var settings: GameSettings = GameSettings.DEFAULT
    private var attempt = 0
    private var activeToken = -1
    private var sinceLandMs = Long.MAX_VALUE
    private var nearMissAwarded = true

    fun enterRoute(token: Int) {
        stopLoop()
        if (token == activeToken && _state.value.phase != GamePhase.ENDED && _state.value.lanes.isNotEmpty()) {
            if (!_state.value.paused) startLoop()
            return
        }
        activeToken = token
        settings = settingsRepository.load()
        attempt += 1
        val cache = drillRepository.featherCache()
        val total = (settings.startingFeathers + cache).coerceIn(1, GameConfig.MAX_FEATHERS)
        val seed = generateRoute.seedFor(System.currentTimeMillis() / DAY_MS, attempt)
        val lanes = ArrayList<Lane>()
        lanes.addAll(generateRoute.chunk(0, seed, 1f, settings.reaction.gapMul, true))
        lanes.addAll(generateRoute.chunk(lanes.size, seed + 17L, 1f, settings.reaction.gapMul, false))
        sinceLandMs = Long.MAX_VALUE
        nearMissAwarded = true
        _state.value = GameUiState(
            phase = GamePhase.IDLE,
            lanes = lanes,
            birdLane = 0,
            birdX = RouteMath.BOARD_UNITS / 2f,
            bottomLane = 0,
            feathers = total,
            totalFeathers = total,
            recoveryTotalMs = settings.recovery.millis,
            reduceMotion = settings.reduceMotion,
            highContrast = settings.highContrast,
            hintLeftMs = GameConfig.RUN_HINT_MS,
            seed = seed
        )
        startLoop()
    }

    fun leaveRoute() {
        stopLoop()
    }

    fun pause() {
        if (_state.value.phase == GamePhase.ENDED) return
        stopLoop()
        _state.update { it.copy(paused = true, holding = false) }
    }

    fun resume() {
        if (_state.value.phase == GamePhase.ENDED) return
        _state.update { it.copy(paused = false) }
        startLoop()
    }

    fun setHolding(holding: Boolean) {
        val current = _state.value
        if (current.paused || current.phase == GamePhase.ENDED) return
        _state.update { it.copy(holding = holding) }
    }

    fun hop() {
        val current = _state.value
        if (current.paused) return
        if (current.phase != GamePhase.IDLE && current.phase != GamePhase.RUNNING) return
        if (current.holding) return

        val targetIndex = current.birdLane + 1
        extendRoute(targetIndex)
        val staged = _state.value
        val target = staged.lanes.getOrNull(targetIndex) ?: return

        if (settings.holdAssist && !RouteMath.safeToEnter(target, staged.birdX, ASSIST_LOOKAHEAD)) {
            _state.update { it.copy(blockedFlashMs = 260L) }
            return
        }

        sinceLandMs = 0L
        nearMissAwarded = false

        val crossed = staged.lanesCrossed + 1
        val gained = (GameConfig.LANE_SCORE * staged.multiplier).toInt()
        var score = staged.score + gained
        var banked = staged.banked
        var checkpointFlash = staged.checkpointFlashMs
        var surge = staged.surgeLeftMs
        if (crossed % GameConfig.CHECKPOINT_EVERY == 0) {
            score += GameConfig.CHECKPOINT_BONUS
            banked = score
            checkpointFlash = GameConfig.FLASH_MS
        }
        if (crossed % GameConfig.SURGE_EVERY == 0) {
            surge = GameConfig.SURGE_DURATION_MS
        }

        var multiplier = staged.multiplier
        var parcels = staged.parcels
        var parcelFlash = staged.parcelFlashMs
        var lanes = staged.lanes
        val parcelX = target.parcelX
        if (parcelX != null && !target.parcelTaken && abs(parcelX - staged.birdX) < PARCEL_REACH) {
            lanes = lanes.toMutableList().also { it[targetIndex] = target.copy(parcelTaken = true) }
            multiplier = nextMultiplier(multiplier)
            parcels += 1
            parcelFlash = GameConfig.FLASH_MS
        }

        val streak = staged.streak + 1
        _state.value = staged.copy(
            phase = GamePhase.RUNNING,
            lanes = lanes,
            birdLane = targetIndex,
            bottomLane = max(0, targetIndex - GameConfig.LANES_BELOW_BIRD),
            lanesCrossed = crossed,
            score = score,
            banked = banked,
            multiplier = multiplier,
            parcels = parcels,
            parcelFlashMs = parcelFlash,
            checkpointFlashMs = checkpointFlash,
            surgeLeftMs = surge,
            streak = streak,
            bestStreak = max(staged.bestStreak, streak),
            driftLeftMs = if (target.kind == LaneKind.RIVER) GameConfig.FERRY_DRIFT_MS else 0L,
            hintLeftMs = staged.hintLeftMs
        )
    }

    fun burst() {
        val current = _state.value
        if (current.phase != GamePhase.RECOVERING || current.feathers <= 0) return
        val safeLane = (current.birdLane downTo 0)
            .firstOrNull { current.lanes.getOrNull(it)?.kind == LaneKind.KERB } ?: 0
        sinceLandMs = Long.MAX_VALUE
        nearMissAwarded = true
        _state.value = current.copy(
            phase = GamePhase.RUNNING,
            feathers = current.feathers - 1,
            burstsSpent = current.burstsSpent + 1,
            multiplier = 1f,
            slipstream = 0,
            streak = 0,
            birdLane = safeLane,
            birdX = RouteMath.BOARD_UNITS / 2f,
            bottomLane = max(0, safeLane - GameConfig.LANES_BELOW_BIRD),
            recoveryLeftMs = 0L,
            driftLeftMs = 0L,
            burstFlashMs = 420L,
            endReason = null
        )
    }

    private fun startLoop() {
        stopLoop()
        loop = viewModelScope.launch {
            while (isActive) {
                delay(GameConfig.TICK_MS)
                tick()
            }
        }
    }

    private fun stopLoop() {
        loop?.cancel()
        loop = null
    }

    private fun tick() {
        val current = _state.value
        if (current.paused || current.phase == GamePhase.ENDED) return

        val dtMs = GameConfig.TICK_MS
        val scale = if (current.phase == GamePhase.RECOVERING) GameConfig.RECOVER_TIME_SCALE else 1f
        val dtSec = dtMs / 1000f * scale
        val speedMul = speedMultiplier(current)

        val low = max(0, current.bottomLane - 1)
        val high = current.bottomLane + GameConfig.VISIBLE_LANES + 2
        val lanes = current.lanes.mapIndexed { index, lane ->
            if (index in low..high && lane.hazardCount > 0) {
                lane.copy(
                    offset = RouteMath.wrap(
                        lane.offset + lane.speed * speedMul * dtSec,
                        RouteMath.SPAN
                    )
                )
            } else {
                lane
            }
        }

        var next = current.copy(
            lanes = lanes,
            hintLeftMs = (current.hintLeftMs - dtMs).coerceAtLeast(0L),
            surgeLeftMs = (current.surgeLeftMs - dtMs).coerceAtLeast(0L),
            blockedFlashMs = (current.blockedFlashMs - dtMs).coerceAtLeast(0L),
            nearMissFlashMs = (current.nearMissFlashMs - dtMs).coerceAtLeast(0L),
            parcelFlashMs = (current.parcelFlashMs - dtMs).coerceAtLeast(0L),
            checkpointFlashMs = (current.checkpointFlashMs - dtMs).coerceAtLeast(0L),
            burstFlashMs = (current.burstFlashMs - dtMs).coerceAtLeast(0L)
        )

        if (sinceLandMs < Long.MAX_VALUE - dtMs) sinceLandMs += dtMs

        if (current.phase == GamePhase.RECOVERING) {
            val left = current.recoveryLeftMs - dtMs
            if (left <= 0L) {
                finish(next, current.endReason ?: EndReason.HAZARD)
                return
            }
            _state.value = next.copy(recoveryLeftMs = left)
            return
        }

        val lane = lanes.getOrNull(next.birdLane)
        if (lane == null) {
            _state.value = next
            return
        }

        var birdX = next.birdX
        var drift = next.driftLeftMs
        var failure: EndReason? = null

        when (lane.kind) {
            LaneKind.RIVER -> {
                if (RouteMath.carrierUnderBird(lane, birdX) == null) {
                    failure = EndReason.WATER
                } else {
                    birdX += RouteMath.laneVelocity(lane) * speedMul * dtSec
                    drift = (drift - dtMs).coerceAtLeast(0L)
                    if (birdX < EDGE_MARGIN || birdX > RouteMath.BOARD_UNITS - EDGE_MARGIN) {
                        failure = EndReason.WATER
                    } else if (drift <= 0L) {
                        failure = EndReason.WATER
                    }
                }
            }

            LaneKind.ROAD, LaneKind.RAIL -> {
                if (RouteMath.overlapsBird(lane, birdX)) failure = EndReason.HAZARD
            }

            LaneKind.KERB -> drift = 0L
        }

        next = next.copy(birdX = birdX, driftLeftMs = drift, gapMeter = gapMeter(next, lanes))

        if (failure == null && !nearMissAwarded &&
            sinceLandMs <= GameConfig.NEAR_MISS_WINDOW_MS &&
            (lane.kind == LaneKind.ROAD || lane.kind == LaneKind.RAIL)
        ) {
            val distance = RouteMath.nearestGapDistance(lane, birdX)
            if (distance in 0f..RouteMath.NEAR_MISS_RANGE) {
                nearMissAwarded = true
                val surgeBonus = if (next.surgeActive) 2 else 1
                val gain = (GameConfig.NEAR_MISS_SCORE * next.multiplier).toInt() * surgeBonus
                var slipstream = next.slipstream + 1
                var feathers = next.feathers
                var total = next.totalFeathers
                if (slipstream >= GameConfig.SLIPSTREAM_TARGET) {
                    slipstream = 0
                    if (feathers < GameConfig.MAX_FEATHERS) {
                        feathers += 1
                        total = max(total, feathers)
                    }
                }
                next = next.copy(
                    score = next.score + gain,
                    nearMisses = next.nearMisses + 1,
                    slipstream = slipstream,
                    feathers = feathers,
                    totalFeathers = total,
                    nearMissFlashMs = GameConfig.FLASH_MS
                )
            }
        }

        if (failure != null) {
            if (next.feathers > 0) {
                _state.value = next.copy(
                    phase = GamePhase.RECOVERING,
                    endReason = failure,
                    recoveryLeftMs = settings.recovery.millis,
                    recoveryTotalMs = settings.recovery.millis,
                    holding = false,
                    streak = 0
                )
            } else {
                finish(next, EndReason.NO_FEATHERS)
            }
            return
        }

        _state.value = next
    }

    private fun gapMeter(state: GameUiState, lanes: List<Lane>): Float {
        val ahead = lanes.getOrNull(state.birdLane + 1) ?: return 1f
        return RouteMath.gapProgress(ahead, state.birdX)
    }

    private fun speedMultiplier(state: GameUiState): Float {
        val growth = (1f + 0.06f * (state.lanesCrossed / 10f)).coerceAtMost(GameConfig.MAX_SPEED_MUL)
        val surge = if (state.surgeLeftMs > 0L) GameConfig.SURGE_SPEED_BONUS else 1f
        return growth * settings.reaction.speedMul * surge
    }

    private fun nextMultiplier(value: Float): Float = when {
        value < 1.5f -> 1.5f
        value < 2f -> 2f
        else -> 3f
    }

    private fun extendRoute(targetIndex: Int) {
        val current = _state.value
        if (targetIndex + GameConfig.VISIBLE_LANES < current.lanes.size) return
        val speedMul = (1f + 0.06f * (current.lanesCrossed / 10f)).coerceAtMost(GameConfig.MAX_SPEED_MUL)
        val addition = generateRoute.chunk(
            startIndex = current.lanes.size,
            seed = current.seed + current.lanes.size * 31L,
            speedMul = speedMul,
            gapMul = settings.reaction.gapMul,
            openWithKerb = false
        )
        _state.update { it.copy(lanes = it.lanes + addition) }
    }

    private fun finish(state: GameUiState, reason: EndReason) {
        stopLoop()
        val unbanked = max(0, state.score - state.banked)
        val total = state.banked + unbanked / 2
        val result = RunResult(
            score = total,
            lanes = state.lanesCrossed,
            nearMisses = state.nearMisses,
            parcels = state.parcels,
            bestStreak = state.bestStreak,
            banked = state.banked,
            reason = reason
        )
        _state.value = state.copy(
            phase = GamePhase.ENDED,
            endReason = reason,
            recoveryLeftMs = 0L,
            holding = false,
            score = total
        )
        val bursts = state.burstsSpent
        viewModelScope.launch {
            val rank = submitRun(result, bursts)
            _events.send(GameEvent.Finished(result, rank))
        }
    }

    private companion object {
        const val DAY_MS = 86_400_000L
        const val PARCEL_REACH = 120f
        const val EDGE_MARGIN = 70f
        const val ASSIST_LOOKAHEAD = 0.24f
    }
}
