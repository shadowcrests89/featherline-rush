package com.Pupcevi3.Xekqes.presentation.leaderboard

import com.Pupcevi3.Xekqes.domain.model.LeaderboardEntry

enum class LeaderboardFilter { ALL_TIME, TODAY }

data class LeaderboardUiState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val filter: LeaderboardFilter = LeaderboardFilter.ALL_TIME,
    val today: Long = 0L
)
