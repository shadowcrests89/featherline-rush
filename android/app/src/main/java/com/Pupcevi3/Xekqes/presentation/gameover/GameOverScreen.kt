package com.Pupcevi3.Xekqes.presentation.gameover

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.core.di.ServiceLocator
import com.Pupcevi3.Xekqes.core.theme.BigScoreStyle
import com.Pupcevi3.Xekqes.core.theme.GlassBorder
import com.Pupcevi3.Xekqes.core.theme.LaneAmber
import com.Pupcevi3.Xekqes.core.theme.ScarletFeather
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised
import com.Pupcevi3.Xekqes.core.theme.TextMuted
import com.Pupcevi3.Xekqes.core.theme.TextSecondary
import com.Pupcevi3.Xekqes.core.ui.Formatting
import com.Pupcevi3.Xekqes.core.ui.GlassPanel
import com.Pupcevi3.Xekqes.core.ui.PrimaryButton
import com.Pupcevi3.Xekqes.core.ui.ProgressTrack
import com.Pupcevi3.Xekqes.core.ui.ScreenBackground
import com.Pupcevi3.Xekqes.core.ui.SecondaryButton
import com.Pupcevi3.Xekqes.core.ui.StatCard
import com.Pupcevi3.Xekqes.core.ui.drillTitle
import com.Pupcevi3.Xekqes.domain.model.EndReason
import com.Pupcevi3.Xekqes.domain.model.RunResult

@Composable
fun GameOverScreen(
    result: RunResult,
    rank: Int,
    onPlayAgain: () -> Unit,
    onLeaderboard: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = ServiceLocator.factory)
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        gameOverViewModel.load(result)
    }

    val animatedScore by animateIntAsState(
        targetValue = result.score,
        animationSpec = tween(durationMillis = 700),
        label = "scoreCount"
    )

    val transition = rememberInfiniteTransition(label = "recordGlow")
    val glow by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    ScreenBackground(
        overlay = listOf(
            Color(0x26FF2D4A),
            Color(0xE60A0A0F),
            Color(0xF70A0A0F)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = reasonText(result.reason),
                style = MaterialTheme.typography.headlineMedium,
                color = ScarletFeather,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = Formatting.score(animatedScore),
                style = BigScoreStyle,
                color = LaneAmber,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Text(
                text = stringResource(R.string.banked_label, Formatting.score(result.banked)),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextMuted
            )

            if (state.isRecord) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .alpha(glow)
                        .background(
                            LaneAmber.copy(alpha = 0.16f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.new_record),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = LaneAmber
                    )
                    if (rank > 0 && state.totalEntries > 0) {
                        Text(
                            text = stringResource(R.string.rank_of, rank, state.totalEntries),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (result.lanes > 0 || result.nearMisses > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        value = result.lanes.toString(),
                        label = stringResource(R.string.stat_lanes),
                        accent = LaneAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = result.nearMisses.toString(),
                        label = stringResource(R.string.stat_near),
                        accent = ScarletFeather,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (result.parcels > 0 && result.bestStreak > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        value = result.parcels.toString(),
                        label = stringResource(R.string.stat_parcels),
                        accent = LaneAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = result.bestStreak.toString(),
                        label = stringResource(R.string.stat_streak),
                        accent = ScarletFeather,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            val drill = state.drill
            if (drill != null) {
                Spacer(modifier = Modifier.height(14.dp))
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    container = SurfaceRaised.copy(alpha = 0.72f),
                    border = GlassBorder,
                    contentPadding = 12.dp
                ) {
                    Text(
                        text = stringResource(R.string.drill_line, drillTitle(drill.kind)),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ProgressTrack(
                        progress = drill.fraction,
                        accent = LaneAmber,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatting.ratio(drill.progress, drill.target),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            PrimaryButton(
                label = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    label = stringResource(R.string.leaderboard),
                    onClick = onLeaderboard,
                    accent = LaneAmber.copy(alpha = 0.45f),
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    label = stringResource(R.string.menu),
                    onClick = onMenu,
                    accent = ScarletFeather.copy(alpha = 0.45f),
                    leadingIcon = R.drawable.icon_home,
                    iconDescription = stringResource(R.string.cd_home),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun reasonText(reason: EndReason): String = when (reason) {
    EndReason.HAZARD -> stringResource(R.string.over_hazard)
    EndReason.WATER -> stringResource(R.string.over_water)
    EndReason.NO_FEATHERS -> stringResource(R.string.over_feathers)
}
