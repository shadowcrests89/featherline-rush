package com.Pupcevi3.Xekqes.presentation.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Pupcevi3.Xekqes.core.config.GameConfig
import com.Pupcevi3.Xekqes.core.di.ServiceLocator
import com.Pupcevi3.Xekqes.core.ui.ScreenBackground
import com.Pupcevi3.Xekqes.domain.model.RunResult
import com.Pupcevi3.Xekqes.presentation.dialog.PauseDialog

@Composable
fun GameScreen(
    runToken: Int,
    onRouteFinished: (RunResult, Int) -> Unit,
    onMenu: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameViewModel: GameViewModel = viewModel(factory = ServiceLocator.factory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(runToken) {
        gameViewModel.enterRoute(runToken)
        onDispose { gameViewModel.leaveRoute() }
    }

    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onRouteFinished(event.result, event.rank)
            }
        }
    }

    ScreenBackground(
        overlay = listOf(
            Color(0xE60A0A0F),
            Color(0xCC12121F),
            Color(0xF20A0A0F)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHud(
                state = state,
                onPause = gameViewModel::pause
            )

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                val frame = 6.dp
                val boardWidth = minOf(maxWidth, 380.dp)
                val rawLane = (maxHeight - frame * 2).value / GameConfig.VISIBLE_LANES
                val laneHeight = rawLane.toInt().dp.coerceIn(34.dp, 74.dp)
                val boardHeight = laneHeight * GameConfig.VISIBLE_LANES + frame * 2
                LaneBoard(
                    state = state,
                    innerWidth = boardWidth - frame * 2,
                    laneHeight = laneHeight,
                    onTap = gameViewModel::hop,
                    modifier = Modifier.size(width = boardWidth, height = boardHeight)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                FloatingControlBar(
                    state = state,
                    onGo = gameViewModel::hop,
                    onHoldChange = gameViewModel::setHolding,
                    onBurst = gameViewModel::burst
                )
            }
        }
    }

    if (state.paused) {
        PauseDialog(
            onResume = gameViewModel::resume,
            onSettings = onSettings,
            onMenu = onMenu
        )
    }
}
