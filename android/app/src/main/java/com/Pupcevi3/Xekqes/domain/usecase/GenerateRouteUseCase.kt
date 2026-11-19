package com.Pupcevi3.Xekqes.domain.usecase

import com.Pupcevi3.Xekqes.domain.model.Lane
import com.Pupcevi3.Xekqes.domain.model.LaneKind
import com.Pupcevi3.Xekqes.domain.model.RouteMath
import kotlin.math.max
import kotlin.random.Random

class GenerateRouteUseCase {

    fun seedFor(dayIndex: Long, attempt: Int): Long = (dayIndex * 7919L) xor (attempt * 104729L)

    fun chunk(
        startIndex: Int,
        seed: Long,
        speedMul: Float,
        gapMul: Float,
        openWithKerb: Boolean
    ): List<Lane> {
        val random = Random(seed)
        var tries = 0
        while (tries < MAX_TRIES) {
            val candidate = build(startIndex, random, speedMul, gapMul, openWithKerb)
            if (candidate.all { solvable(it, speedMul, gapMul) }) return candidate
            tries++
        }
        return fallback(startIndex, speedMul)
    }

    private fun build(
        startIndex: Int,
        random: Random,
        speedMul: Float,
        gapMul: Float,
        openWithKerb: Boolean
    ): List<Lane> {
        val lanes = ArrayList<Lane>(CHUNK_SIZE)
        var hazardStreak = 0
        for (i in 0 until CHUNK_SIZE) {
            val forceKerb = (i == 0 && openWithKerb) || hazardStreak >= 2
            val kind = if (forceKerb) LaneKind.KERB else pickKind(random)
            hazardStreak = if (kind == LaneKind.KERB) 0 else hazardStreak + 1
            lanes.add(laneOf(startIndex + i, kind, random, speedMul, gapMul))
        }
        return lanes
    }

    private fun pickKind(random: Random): LaneKind {
        val roll = random.nextFloat()
        return when {
            roll < 0.45f -> LaneKind.ROAD
            roll < 0.65f -> LaneKind.RAIL
            roll < 0.80f -> LaneKind.RIVER
            else -> LaneKind.KERB
        }
    }

    private fun laneOf(
        index: Int,
        kind: LaneKind,
        random: Random,
        speedMul: Float,
        gapMul: Float
    ): Lane {
        val direction = if (random.nextBoolean()) 1 else -1
        val variance = 0.85f + random.nextFloat() * 0.40f
        val count = when (kind) {
            LaneKind.ROAD -> 2 + random.nextInt(2)
            LaneKind.RAIL -> 1
            LaneKind.RIVER -> 3
            LaneKind.KERB -> 0
        }
        val speed = when (kind) {
            LaneKind.ROAD -> RouteMath.BASE_SPEED * variance
            LaneKind.RAIL -> RouteMath.BASE_SPEED * RouteMath.RAIL_SPEED_MUL * variance
            LaneKind.RIVER -> RouteMath.BASE_SPEED * RouteMath.RIVER_SPEED_MUL * variance
            LaneKind.KERB -> 0f
        }
        val spacing = if (count <= 0) RouteMath.SPAN else RouteMath.SPAN / count
        val hasParcel = when (kind) {
            LaneKind.KERB -> random.nextFloat() < 0.22f
            LaneKind.RIVER -> random.nextFloat() < 0.35f
            else -> false
        }
        val parcelX = if (hasParcel) 220f + random.nextFloat() * 560f else null
        return Lane(
            index = index,
            kind = kind,
            hazardCount = count,
            spacing = spacing,
            speed = speed * speedMul,
            direction = direction,
            offset = random.nextFloat() * RouteMath.SPAN,
            parcelX = parcelX,
            parcelTaken = false
        )
    }

    private fun solvable(lane: Lane, speedMul: Float, gapMul: Float): Boolean {
        if (lane.kind == LaneKind.KERB) return true
        if (lane.kind == LaneKind.RIVER) return lane.hazardCount >= 2
        val gapUnits = lane.spacing - 2f * RouteMath.halfWidth(lane.kind) - 2f * RouteMath.BIRD_HALF
        if (gapUnits <= 0f) return false
        val speed = max(1f, lane.speed)
        val gapMs = gapUnits / speed * 1000f
        val required = max(260f, 520f / max(0.4f, speedMul)) * gapMul
        return gapMs >= required
    }

    private fun fallback(startIndex: Int, speedMul: Float): List<Lane> {
        val kinds = listOf(
            LaneKind.KERB, LaneKind.ROAD, LaneKind.KERB, LaneKind.RAIL,
            LaneKind.KERB, LaneKind.ROAD, LaneKind.KERB, LaneKind.RIVER
        )
        val random = Random(startIndex.toLong() * 31L + 7L)
        return kinds.mapIndexed { i, kind ->
            val count = when (kind) {
                LaneKind.ROAD -> 2
                LaneKind.RAIL -> 1
                LaneKind.RIVER -> 3
                LaneKind.KERB -> 0
            }
            val speed = when (kind) {
                LaneKind.ROAD -> RouteMath.BASE_SPEED
                LaneKind.RAIL -> RouteMath.BASE_SPEED * RouteMath.RAIL_SPEED_MUL
                LaneKind.RIVER -> RouteMath.BASE_SPEED * RouteMath.RIVER_SPEED_MUL
                LaneKind.KERB -> 0f
            }
            Lane(
                index = startIndex + i,
                kind = kind,
                hazardCount = count,
                spacing = if (count <= 0) RouteMath.SPAN else RouteMath.SPAN / count,
                speed = speed * speedMul,
                direction = if (i % 2 == 0) 1 else -1,
                offset = random.nextFloat() * RouteMath.SPAN,
                parcelX = if (kind == LaneKind.KERB && i == 4) 500f else null,
                parcelTaken = false
            )
        }
    }

    private companion object {
        const val CHUNK_SIZE = 8
        const val MAX_TRIES = 20
    }
}
