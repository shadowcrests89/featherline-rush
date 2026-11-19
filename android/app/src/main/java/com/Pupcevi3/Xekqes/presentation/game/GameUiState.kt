package com.Pupcevi3.Xekqes.presentation.game

import com.Pupcevi3.Xekqes.domain.model.EndReason
import com.Pupcevi3.Xekqes.domain.model.Lane
import com.Pupcevi3.Xekqes.domain.model.LaneKind

enum class GamePhase { IDLE, RUNNING, RECOVERING, ENDED }

data class GameUiState(
    val phase: GamePhase = GamePhase.IDLE,
    val lanes: List<Lane> = emptyList(),
    val birdLane: Int = 0,
    val birdX: Float = 500f,
    val bottomLane: Int = 0,
    val score: Int = 0,
    val banked: Int = 0,
    val lanesCrossed: Int = 0,
    val nearMisses: Int = 0,
    val parcels: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val feathers: Int = 3,
    val totalFeathers: Int = 3,
    val burstsSpent: Int = 0,
    val slipstream: Int = 0,
    val multiplier: Float = 1f,
    val recoveryLeftMs: Long = 0L,
    val recoveryTotalMs: Long = 1600L,
    val surgeLeftMs: Long = 0L,
    val driftLeftMs: Long = 0L,
    val holding: Boolean = false,
    val gapMeter: Float = 1f,
    val blockedFlashMs: Long = 0L,
    val nearMissFlashMs: Long = 0L,
    val parcelFlashMs: Long = 0L,
    val checkpointFlashMs: Long = 0L,
    val burstFlashMs: Long = 0L,
    val hintLeftMs: Long = 6000L,
    val paused: Boolean = false,
    val reduceMotion: Boolean = false,
    val highContrast: Boolean = false,
    val endReason: EndReason? = null,
    val seed: Long = 0L
) {
    val forecast: List<LaneKind>
        get() = (birdLane + 1..birdLane + 3).mapNotNull { lanes.getOrNull(it)?.kind }

    val surgeActive: Boolean get() = surgeLeftMs > 0L

    val canBurst: Boolean get() = phase == GamePhase.RECOVERING && feathers > 0

    val showHint: Boolean get() = phase == GamePhase.IDLE || hintLeftMs > 0L
}
