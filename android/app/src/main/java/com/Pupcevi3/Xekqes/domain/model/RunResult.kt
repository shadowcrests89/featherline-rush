package com.Pupcevi3.Xekqes.domain.model

enum class EndReason { HAZARD, WATER, NO_FEATHERS }

data class RunResult(
    val score: Int,
    val lanes: Int,
    val nearMisses: Int,
    val parcels: Int,
    val bestStreak: Int,
    val banked: Int,
    val reason: EndReason
)

data class LeaderboardEntry(
    val label: String,
    val score: Int,
    val lanes: Int,
    val nearMisses: Int,
    val epochDay: Long,
    val own: Boolean
)
