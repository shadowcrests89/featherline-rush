package com.Pupcevi3.Xekqes.presentation.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.core.config.GameConfig
import com.Pupcevi3.Xekqes.core.theme.AsphaltLane
import com.Pupcevi3.Xekqes.core.theme.DeepBlue
import com.Pupcevi3.Xekqes.core.theme.GlassBorder
import com.Pupcevi3.Xekqes.core.theme.HighContrastAmber
import com.Pupcevi3.Xekqes.core.theme.LaneAmber
import com.Pupcevi3.Xekqes.core.theme.RailLane
import com.Pupcevi3.Xekqes.core.theme.RainStreak
import com.Pupcevi3.Xekqes.core.theme.ScarletFeather
import com.Pupcevi3.Xekqes.core.theme.SurfaceNight
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised
import com.Pupcevi3.Xekqes.core.theme.TextPrimary
import com.Pupcevi3.Xekqes.domain.model.Lane
import com.Pupcevi3.Xekqes.domain.model.LaneKind
import com.Pupcevi3.Xekqes.domain.model.RouteMath

@Composable
fun LaneBoard(
    state: GameUiState,
    innerWidth: Dp,
    laneHeight: Dp,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val transition = rememberInfiniteTransition(label = "boardLoops")
    val blink by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )
    val rain by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "boardRain"
    )

    val laneAccent = if (state.highContrast) HighContrastAmber else LaneAmber
    val rowOfBird = GameConfig.VISIBLE_LANES - 1 - (state.birdLane - state.bottomLane)
    val birdSize = laneHeight * 0.68f
    val targetTop = laneHeight * rowOfBird + (laneHeight - birdSize) / 2
    val birdTop by animateDpAsState(
        targetValue = targetTop,
        animationSpec = if (state.reduceMotion) {
            tween(durationMillis = 120, easing = LinearEasing)
        } else {
            spring(dampingRatio = 0.6f, stiffness = 420f)
        },
        label = "birdHop"
    )
    val birdLeft = innerWidth * (state.birdX / RouteMath.BOARD_UNITS) - birdSize / 2

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = SurfaceNight.copy(alpha = 0.90f),
        border = BorderStroke(2.dp, GlassBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onTap
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0 until GameConfig.VISIBLE_LANES) {
                    val laneIndex = state.bottomLane + GameConfig.VISIBLE_LANES - 1 - row
                    val lane = state.lanes.getOrNull(laneIndex)
                    LaneRow(
                        lane = lane,
                        laneHeight = laneHeight,
                        innerWidth = innerWidth,
                        accent = laneAccent,
                        blink = blink,
                        highContrast = state.highContrast
                    )
                }
            }

            if (!state.reduceMotion) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val columns = 14
                    val stepX = size.width / columns
                    for (i in 0 until columns) {
                        val phase = (rain + i * 0.17f) % 1f
                        drawRoundRect(
                            color = RainStreak.copy(alpha = 0.18f),
                            topLeft = Offset(
                                i * stepX + stepX * 0.5f,
                                phase * (size.height + 120f) - 60f
                            ),
                            size = Size(3f, 40f),
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                    }
                }
            }

            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = stringResource(R.string.cd_bird),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(x = birdLeft, y = birdTop)
                    .size(birdSize)
            )

            if (state.phase == GamePhase.RECOVERING) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ScarletFeather.copy(alpha = 0.32f),
                                    Color.Transparent,
                                    ScarletFeather.copy(alpha = 0.32f)
                                )
                            )
                        )
                )
                Text(
                    text = stringResource(R.string.recovery_label),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = ScarletFeather,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(SurfaceNight.copy(alpha = 0.86f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            if (state.checkpointFlashMs > 0L) {
                BoardFloater(
                    text = stringResource(R.string.checkpoint),
                    color = laneAccent,
                    alpha = state.checkpointFlashMs / GameConfig.FLASH_MS.toFloat(),
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            if (state.nearMissFlashMs > 0L) {
                BoardFloater(
                    text = stringResource(R.string.near_miss, GameConfig.NEAR_MISS_SCORE),
                    color = laneAccent,
                    alpha = state.nearMissFlashMs / GameConfig.FLASH_MS.toFloat(),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            if (state.parcelFlashMs > 0L) {
                BoardFloater(
                    text = stringResource(R.string.parcel_gain),
                    color = ScarletFeather,
                    alpha = state.parcelFlashMs / GameConfig.FLASH_MS.toFloat(),
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            if (state.showHint) {
                Text(
                    text = stringResource(R.string.game_hint),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp
                    ),
                    color = TextPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .background(SurfaceNight.copy(alpha = 0.80f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun BoardFloater(
    text: String,
    color: Color,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        color = color,
        modifier = modifier
            .padding(10.dp)
            .alpha(alpha.coerceIn(0f, 1f))
            .background(SurfaceNight.copy(alpha = 0.80f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun LaneRow(
    lane: Lane?,
    laneHeight: Dp,
    innerWidth: Dp,
    accent: Color,
    blink: Float,
    highContrast: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(laneHeight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (lane?.kind) {
                LaneKind.ROAD -> {
                    drawRect(color = AsphaltLane)
                    val dashWidth = size.width / 16f
                    var x = 0f
                    while (x < size.width) {
                        drawRoundRect(
                            color = accent.copy(alpha = 0.70f),
                            topLeft = Offset(x, size.height / 2f - 2f),
                            size = Size(dashWidth * 0.6f, 4f),
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                        x += dashWidth
                    }
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color(0x0AFFFFFF), Color.Transparent)
                        )
                    )
                }

                LaneKind.RAIL -> {
                    drawRect(color = RailLane)
                    var sleeper = 0f
                    while (sleeper < size.width) {
                        drawRect(
                            color = SurfaceRaised,
                            topLeft = Offset(sleeper, size.height * 0.24f),
                            size = Size(6f, size.height * 0.52f)
                        )
                        sleeper += size.width / 14f
                    }
                    drawRect(
                        color = RainStreak,
                        topLeft = Offset(0f, size.height * 0.34f),
                        size = Size(size.width, 3f)
                    )
                    drawRect(
                        color = RainStreak,
                        topLeft = Offset(0f, size.height * 0.62f),
                        size = Size(size.width, 3f)
                    )
                }

                LaneKind.RIVER -> {
                    drawRect(color = DeepBlue)
                    for (i in 0 until 3) {
                        drawRect(
                            color = RainStreak.copy(alpha = 0.55f),
                            topLeft = Offset(
                                (size.width * ((blink + i * 0.3f) % 1f)) - size.width * 0.1f,
                                size.height * (0.26f + i * 0.2f)
                            ),
                            size = Size(size.width * 0.22f, 2f)
                        )
                    }
                }

                else -> {
                    drawRect(color = SurfaceRaised)
                    drawRect(
                        color = accent.copy(alpha = if (highContrast) 0.9f else 0.6f),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, 2f)
                    )
                }
            }
        }

        if (lane != null && lane.kind == LaneKind.RAIL) {
            val warn = RouteMath.nearestGapDistance(lane, RouteMath.BOARD_UNITS / 2f)
            if (warn in 0f..520f && blink < 0.5f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp)
                        .size(8.dp)
                        .background(ScarletFeather, RoundedCornerShape(4.dp))
                )
            }
        }

        if (lane != null) {
            val parcelX = lane.parcelX
            if (parcelX != null && !lane.parcelTaken) {
                val parcelSize = laneHeight * 0.44f
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = stringResource(R.string.cd_parcel),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(
                            x = innerWidth * (parcelX / RouteMath.BOARD_UNITS) - parcelSize / 2,
                            y = (laneHeight - parcelSize) / 2
                        )
                        .size(parcelSize)
                )
            }
            HazardLayer(
                lane = lane,
                laneHeight = laneHeight,
                innerWidth = innerWidth,
                highContrast = highContrast
            )
        }
    }
}

@Composable
private fun HazardLayer(
    lane: Lane,
    laneHeight: Dp,
    innerWidth: Dp,
    highContrast: Boolean
) {
    if (lane.hazardCount <= 0) return
    val sprite = when (lane.kind) {
        LaneKind.ROAD -> R.drawable.sprite_2
        LaneKind.RAIL -> R.drawable.sprite_3
        LaneKind.RIVER -> R.drawable.sprite_4
        LaneKind.KERB -> R.drawable.sprite_2
    }
    val description = when (lane.kind) {
        LaneKind.ROAD -> stringResource(R.string.cd_van)
        LaneKind.RAIL -> stringResource(R.string.cd_tram)
        else -> stringResource(R.string.cd_ferry)
    }
    val widthUnits = RouteMath.halfWidth(lane.kind) * 2f
    val hazardWidth = innerWidth * (widthUnits / RouteMath.BOARD_UNITS)
    val hazardHeight = laneHeight * 0.78f
    RouteMath.hazardPositions(lane).forEach { centre ->
        val left = innerWidth * (centre / RouteMath.BOARD_UNITS) - hazardWidth / 2
        Image(
            painter = painterResource(sprite),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = left, y = (laneHeight - hazardHeight) / 2)
                .size(width = hazardWidth, height = hazardHeight)
                .alpha(if (highContrast) 1f else 0.98f)
                .graphicsLayer { scaleX = if (lane.direction < 0) -1f else 1f }
        )
    }
}
