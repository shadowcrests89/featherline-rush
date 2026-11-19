package com.Pupcevi3.Xekqes.core.navigation

import com.Pupcevi3.Xekqes.domain.model.RunResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Game : Screen
    data class GameOver(val result: RunResult, val rank: Int) : Screen
    data object Leaderboard : Screen
    data object Settings : Screen
}
