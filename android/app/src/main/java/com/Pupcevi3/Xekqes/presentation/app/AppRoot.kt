package com.Pupcevi3.Xekqes.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Pupcevi3.Xekqes.core.di.ServiceLocator
import com.Pupcevi3.Xekqes.core.navigation.Screen
import com.Pupcevi3.Xekqes.core.theme.AsphaltBlack
import com.Pupcevi3.Xekqes.presentation.game.GameScreen
import com.Pupcevi3.Xekqes.presentation.gameover.GameOverScreen
import com.Pupcevi3.Xekqes.presentation.leaderboard.LeaderboardScreen
import com.Pupcevi3.Xekqes.presentation.menu.MenuScreen
import com.Pupcevi3.Xekqes.presentation.settings.SettingsScreen
import com.Pupcevi3.Xekqes.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val runToken by appViewModel.runToken.collectAsStateWithLifecycle()

    BackHandler(enabled = screen !is Screen.Menu && screen !is Screen.Splash) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AsphaltBlack)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 340)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 240))
            },
            label = "screenHost"
        ) { current ->
            when (current) {
                is Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.replace(Screen.Menu) }
                )

                is Screen.Menu -> MenuScreen(
                    onPlay = { appViewModel.open(Screen.Game) },
                    onLeaderboard = { appViewModel.open(Screen.Leaderboard) },
                    onSettings = { appViewModel.open(Screen.Settings) }
                )

                is Screen.Game -> GameScreen(
                    runToken = runToken,
                    onRouteFinished = { result, rank ->
                        appViewModel.replace(Screen.GameOver(result, rank))
                    },
                    onMenu = { appViewModel.home() },
                    onSettings = { appViewModel.open(Screen.Settings) }
                )

                is Screen.GameOver -> GameOverScreen(
                    result = current.result,
                    rank = current.rank,
                    onPlayAgain = { appViewModel.replace(Screen.Game) },
                    onLeaderboard = { appViewModel.open(Screen.Leaderboard) },
                    onMenu = { appViewModel.home() }
                )

                is Screen.Leaderboard -> LeaderboardScreen(
                    onMenu = { appViewModel.home() },
                    onPlay = { appViewModel.replace(Screen.Game) }
                )

                is Screen.Settings -> SettingsScreen(
                    onMenu = { appViewModel.home() }
                )
            }
        }
    }
}
