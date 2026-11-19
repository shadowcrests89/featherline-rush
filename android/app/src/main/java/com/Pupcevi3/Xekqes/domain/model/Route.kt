package com.Pupcevi3.Xekqes.domain.model

enum class LaneKind { KERB, ROAD, RAIL, RIVER }

data class Lane(
    val index: Int,
    val kind: LaneKind,
    val hazardCount: Int,
    val spacing: Float,
    val speed: Float,
    val direction: Int,
    val offset: Float,
    val parcelX: Float?,
    val parcelTaken: Boolean
)

object RouteMath {

    const val BOARD_UNITS = 1000f
    const val BOARD_MARGIN = 360f
    const val SPAN = BOARD_UNITS + 2f * BOARD_MARGIN
    const val BIRD_HALF = 54f
    const val VAN_HALF = 96f
    const val TRAM_HALF = 148f
    const val FERRY_HALF = 132f
    const val PARCEL_HALF = 48f
    const val NEAR_MISS_RANGE = 78f
    const val BASE_SPEED = 250f
    const val RAIL_SPEED_MUL = 2.1f
    const val RIVER_SPEED_MUL = 0.55f

    fun wrap(value: Float, span: Float): Float {
        var v = value % span
        if (v < 0f) v += span
        return v
    }

    fun halfWidth(kind: LaneKind): Float = when (kind) {
        LaneKind.ROAD -> VAN_HALF
        LaneKind.RAIL -> TRAM_HALF
        LaneKind.RIVER -> FERRY_HALF
        LaneKind.KERB -> 0f
    }

    fun hazardX(lane: Lane, index: Int): Float {
        val raw = wrap(index * lane.spacing + lane.offset, SPAN) - BOARD_MARGIN
        return if (lane.direction > 0) raw else BOARD_UNITS - raw
    }

    fun hazardPositions(lane: Lane): List<Float> =
        if (lane.hazardCount <= 0) emptyList() else (0 until lane.hazardCount).map { hazardX(lane, it) }

    fun laneVelocity(lane: Lane): Float = lane.speed * lane.direction

    fun overlapsBird(lane: Lane, birdX: Float): Boolean {
        val reach = halfWidth(lane.kind) + BIRD_HALF
        return hazardPositions(lane).any { kotlin.math.abs(it - birdX) < reach }
    }

    fun carrierUnderBird(lane: Lane, birdX: Float): Float? {
        val reach = halfWidth(lane.kind) - BIRD_HALF * 0.4f
        return hazardPositions(lane).firstOrNull { kotlin.math.abs(it - birdX) < reach }
    }

    fun nearestGapDistance(lane: Lane, birdX: Float): Float {
        val reach = halfWidth(lane.kind) + BIRD_HALF
        val distances = hazardPositions(lane).map { kotlin.math.abs(it - birdX) - reach }
        return distances.minOrNull() ?: Float.MAX_VALUE
    }

    fun safeToEnter(lane: Lane, birdX: Float, lookAheadSec: Float): Boolean {
        if (lane.kind == LaneKind.KERB) return true
        if (lane.kind == LaneKind.RIVER) return carrierUnderBird(lane, birdX) != null
        val reach = halfWidth(lane.kind) + BIRD_HALF
        var step = 0f
        while (step <= lookAheadSec) {
            val future = lane.copy(offset = lane.offset + lane.speed * step)
            if (hazardPositions(future).any { kotlin.math.abs(it - birdX) < reach }) return false
            step += 0.08f
        }
        return true
    }

    fun gapProgress(lane: Lane, birdX: Float): Float {
        if (lane.kind == LaneKind.KERB) return 1f
        val distance = nearestGapDistance(lane, birdX)
        if (distance >= 420f) return 1f
        if (distance <= 0f) return 0f
        return (distance / 420f).coerceIn(0f, 1f)
    }
}
