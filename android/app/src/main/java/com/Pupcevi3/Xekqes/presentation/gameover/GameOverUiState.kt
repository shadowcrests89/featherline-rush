package com.Pupcevi3.Xekqes.presentation.gameover

import com.Pupcevi3.Xekqes.domain.model.DailyDrill

data class GameOverUiState(
    val bestScore: Int = 0,
    val isRecord: Boolean = false,
    val totalEntries: Int = 0,
    val drill: DailyDrill? = null
)
