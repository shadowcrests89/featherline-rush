package com.Pupcevi3.Xekqes.presentation.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.core.config.GameConfig
import com.Pupcevi3.Xekqes.core.theme.GlassBorder
import com.Pupcevi3.Xekqes.core.theme.LaneAmber
import com.Pupcevi3.Xekqes.core.theme.ScoreStyle
import com.Pupcevi3.Xekqes.core.theme.SurfaceNight
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised
import com.Pupcevi3.Xekqes.core.theme.TextMuted
import com.Pupcevi3.Xekqes.core.theme.TextSecondary
import com.Pupcevi3.Xekqes.core.ui.FeatherPips
import com.Pupcevi3.Xekqes.core.ui.Formatting
import com.Pupcevi3.Xekqes.core.ui.ProgressTrack
import com.Pupcevi3.Xekqes.core.ui.laneIcon
import com.Pupcevi3.Xekqes.core.ui.laneLabel

@Composable
fun GameHud(
    state: GameUiState,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceNight.copy(alpha = 0.72f),
        shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SurfaceRaised.copy(alpha = 0.80f), RoundedCornerShape(12.dp))
                        .clickable(onClick = onPause),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = stringResource(R.string.cd_pause),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = Formatting.score(state.score),
                        style = ScoreStyle,
                        color = LaneAmber,
                        modifier = Modifier.semantics {
                            liveRegion = LiveRegionMode.Polite
                        }
                    )
                    Text(
                        text = stringResource(R.string.game_lane, state.lanesCrossed),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    FeatherPips(
                        total = state.totalFeathers,
                        remaining = state.feathers,
                        state = stringResource(
                            R.string.feathers_state,
                            state.feathers,
                            state.totalFeathers
                        )
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .width(54.dp)
                    ) {
                        ProgressTrack(
                            progress = state.slipstream / GameConfig.SLIPSTREAM_TARGET.toFloat(),
                            accent = LaneAmber,
                            thickness = 3
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(26.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.forecast_title),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = TextMuted
                )
                state.forecast.forEach { kind ->
                    Row(
                        modifier = Modifier
                            .background(
                                SurfaceRaised.copy(alpha = 0.70f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(laneIcon(kind)),
                            contentDescription = stringResource(R.string.cd_lane_icon),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = laneLabel(kind),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = TextSecondary
                        )
                    }
                }
                if (state.surgeActive) {
                    Text(
                        text = stringResource(
                            R.string.surge,
                            (state.surgeLeftMs / 1000L).toInt()
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color(0xFFFF6B3D),
                        modifier = Modifier
                            .semantics { stateDescription = stringSurge(state) }
                    )
                }
                if (state.multiplier > 1f) {
                    Text(
                        text = stringResource(
                            R.string.multiplier,
                            formatMultiplier(state.multiplier)
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = LaneAmber
                    )
                }
            }
        }
    }
}

private fun stringSurge(state: GameUiState): String = (state.surgeLeftMs / 1000L).toString()

private fun formatMultiplier(value: Float): String =
    if (value == value.toInt().toFloat()) value.toInt().toString() else value.toString()
