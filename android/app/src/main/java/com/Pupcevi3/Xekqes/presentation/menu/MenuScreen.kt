package com.Pupcevi3.Xekqes.presentation.menu

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.core.di.ServiceLocator
import com.Pupcevi3.Xekqes.core.theme.GlassBorder
import com.Pupcevi3.Xekqes.core.theme.LaneAmber
import com.Pupcevi3.Xekqes.core.theme.ScarletFeather
import com.Pupcevi3.Xekqes.core.theme.SurfaceNight
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised
import com.Pupcevi3.Xekqes.core.theme.TextMuted
import com.Pupcevi3.Xekqes.core.theme.TextPrimary
import com.Pupcevi3.Xekqes.core.theme.TextSecondary
import com.Pupcevi3.Xekqes.core.ui.Formatting
import com.Pupcevi3.Xekqes.core.ui.PrimaryButton
import com.Pupcevi3.Xekqes.core.ui.ProgressTrack
import com.Pupcevi3.Xekqes.core.ui.ScreenBackground
import com.Pupcevi3.Xekqes.core.ui.SecondaryButton
import com.Pupcevi3.Xekqes.core.ui.StatCard
import com.Pupcevi3.Xekqes.core.ui.drillTitle
import com.Pupcevi3.Xekqes.domain.model.DailyDrill
import com.Pupcevi3.Xekqes.domain.model.DrillKind
import com.Pupcevi3.Xekqes.presentation.dialog.DrillDetailDialog

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        menuViewModel.reload()
        onDispose { }
    }

    MenuContent(
        state = state,
        onPlay = onPlay,
        onLeaderboard = onLeaderboard,
        onSettings = onSettings,
        onClaim = menuViewModel::claim,
        onOpenDetail = menuViewModel::openDetail,
        modifier = modifier
    )

    val detail = state.detail
    if (detail != null) {
        val drill = state.drills.firstOrNull { it.kind == detail }
        if (drill != null) {
            DrillDetailDialog(
                drill = drill,
                onClose = menuViewModel::closeDetail
            )
        }
    }
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    onClaim: (DrillKind) -> Unit,
    onOpenDetail: (DrillKind) -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenBackground(
        overlay = listOf(
            Color(0x990A0A0F),
            Color(0x330A0A0F),
            Color(0xF212121F)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            MenuArtwork(modifier = Modifier.weight(1f))
            MenuSheet(
                state = state,
                onPlay = onPlay,
                onLeaderboard = onLeaderboard,
                onSettings = onSettings,
                onClaim = onClaim,
                onOpenDetail = onOpenDetail
            )
        }
    }
}

@Composable
private fun MenuArtwork(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "menuArt")
    val hover by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hover"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LaneAmber.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.72f, size.height * 0.52f),
                    radius = size.minDimension * 0.46f
                ),
                radius = size.minDimension * 0.46f,
                center = Offset(size.width * 0.72f, size.height * 0.52f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ScarletFeather.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(size.width * 0.26f, size.height * 0.72f),
                    radius = size.minDimension * 0.38f
                ),
                radius = size.minDimension * 0.38f,
                center = Offset(size.width * 0.26f, size.height * 0.72f)
            )
        }
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.cd_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 12.dp)
                .size(64.dp)
        )
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.cd_bird),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 60.dp)
                .size(120.dp)
                .graphicsLayer { translationY = hover }
        )
    }
}

@Composable
private fun MenuSheet(
    state: MenuUiState,
    onPlay: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    onClaim: (DrillKind) -> Unit,
    onOpenDetail: (DrillKind) -> Unit
) {
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 28.dp, shape = shape, spotColor = ScarletFeather),
        shape = shape,
        color = SurfaceNight.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(4.dp)
                    .background(Color(0x24FFFFFF), CircleShape)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.menu_tagline),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                color = TextSecondary
            )

            if (state.bestScore > 0 && state.bestLanes > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        value = Formatting.score(state.bestScore),
                        label = stringResource(R.string.stat_best_score),
                        accent = LaneAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = state.bestLanes.toString(),
                        label = stringResource(R.string.stat_best_lanes),
                        accent = ScarletFeather,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            DrillsCard(
                state = state,
                onClaim = onClaim,
                onOpenDetail = onOpenDetail
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.menu_hint),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 1.4.sp
                ),
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            PrimaryButton(
                label = stringResource(R.string.play),
                onClick = onPlay,
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
                    label = stringResource(R.string.settings),
                    onClick = onSettings,
                    accent = ScarletFeather.copy(alpha = 0.45f),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DrillsCard(
    state: MenuUiState,
    onClaim: (DrillKind) -> Unit,
    onOpenDetail: (DrillKind) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceRaised.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(LaneAmber)
            )
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.drills_title),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = LaneAmber
                    )
                    Text(
                        text = stringResource(
                            R.string.drills_reset,
                            state.resetHours,
                            state.resetMinutes
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = TextMuted
                    )
                }
                state.drills.forEach { drill ->
                    DrillRow(
                        drill = drill,
                        onClaim = { onClaim(drill.kind) },
                        onOpen = { onOpenDetail(drill.kind) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DrillRow(
    drill: DailyDrill,
    onClaim: () -> Unit,
    onOpen: () -> Unit
) {
    val title = drillTitle(drill.kind)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 34.dp)
            .clickable(onClick = onOpen)
            .semantics { stateDescription = title },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp
                ),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            ProgressTrack(
                progress = drill.fraction,
                accent = if (drill.claimed) ScarletFeather else LaneAmber,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        when {
            drill.claimed -> Text(
                text = stringResource(R.string.claimed),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = ScarletFeather
            )

            drill.complete -> TextButton(onClick = onClaim) {
                Text(
                    text = stringResource(R.string.claim),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = LaneAmber
                )
            }

            else -> Text(
                text = Formatting.ratio(drill.progress, drill.target),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = TextMuted
            )
        }
    }
}

