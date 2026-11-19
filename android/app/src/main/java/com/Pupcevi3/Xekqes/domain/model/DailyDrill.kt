package com.Pupcevi3.Xekqes.domain.model

enum class DrillKind { LANES, NEAR_MISSES, PARCELS, CLEAN_RUN }

data class DailyDrill(
    val kind: DrillKind,
    val target: Int,
    val progress: Int,
    val claimed: Boolean
) {
    val complete: Boolean get() = progress >= target
    val fraction: Float get() = if (target <= 0) 0f else (progress.toFloat() / target).coerceIn(0f, 1f)
}
