package com.Pupcevi3.Xekqes.domain.model

enum class ReactionWindow(val gapMul: Float, val speedMul: Float) {
    RELAXED(1.35f, 0.80f),
    STANDARD(1.00f, 1.00f),
    SHARP(0.85f, 1.12f)
}

enum class RecoveryWindow(val millis: Long) {
    SHORT(1000L),
    MEDIUM(1600L),
    LONG(2200L)
}

data class GameSettings(
    val reaction: ReactionWindow = ReactionWindow.STANDARD,
    val recovery: RecoveryWindow = RecoveryWindow.MEDIUM,
    val holdAssist: Boolean = false,
    val startingFeathers: Int = 3,
    val reduceMotion: Boolean = false,
    val highContrast: Boolean = false
) {
    companion object {
        val DEFAULT = GameSettings()
        const val MIN_FEATHERS = 2
        const val MAX_START_FEATHERS = 4
    }
}
