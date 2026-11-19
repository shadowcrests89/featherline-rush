package com.Pupcevi3.Xekqes.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.Pupcevi3.Xekqes.domain.repository.DrillRepository
import com.Pupcevi3.Xekqes.domain.repository.ScoreRepository
import com.Pupcevi3.Xekqes.domain.repository.SettingsRepository
import com.Pupcevi3.Xekqes.domain.usecase.GenerateRouteUseCase
import com.Pupcevi3.Xekqes.domain.usecase.SubmitRunUseCase
import com.Pupcevi3.Xekqes.presentation.app.AppViewModel
import com.Pupcevi3.Xekqes.presentation.game.GameViewModel
import com.Pupcevi3.Xekqes.presentation.gameover.GameOverViewModel
import com.Pupcevi3.Xekqes.presentation.leaderboard.LeaderboardViewModel
import com.Pupcevi3.Xekqes.presentation.menu.MenuViewModel
import com.Pupcevi3.Xekqes.presentation.settings.SettingsViewModel
import com.Pupcevi3.Xekqes.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val scoreRepository: ScoreRepository,
    private val settingsRepository: SettingsRepository,
    private val drillRepository: DrillRepository,
    private val generateRoute: GenerateRouteUseCase,
    private val submitRun: SubmitRunUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(scoreRepository, drillRepository)
            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(settingsRepository, drillRepository, generateRoute, submitRun)
            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(scoreRepository, drillRepository)
            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) ->
                LeaderboardViewModel(scoreRepository)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settingsRepository)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
