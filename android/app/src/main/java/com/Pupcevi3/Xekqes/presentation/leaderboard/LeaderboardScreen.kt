package com.Pupcevi3.Xekqes.presentation.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.core.di.ServiceLocator
import com.Pupcevi3.Xekqes.core.theme.GlassBorder
import com.Pupcevi3.Xekqes.core.theme.LaneAmber
import com.Pupcevi3.Xekqes.core.theme.ScarletFeather
import com.Pupcevi3.Xekqes.core.theme.ScoreStyle
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised
import com.Pupcevi3.Xekqes.core.theme.TextMuted
import com.Pupcevi3.Xekqes.core.theme.TextPrimary
import com.Pupcevi3.Xekqes.core.theme.TextSecondary
import com.Pupcevi3.Xekqes.core.ui.Formatting
import com.Pupcevi3.Xekqes.core.ui.PrimaryButton
import com.Pupcevi3.Xekqes.core.ui.ScreenBackground
import com.Pupcevi3.Xekqes.core.ui.SecondaryButton
import com.Pupcevi3.Xekqes.core.ui.SectionHeader
import com.Pupcevi3.Xekqes.core.ui.SegmentedSelector
import com.Pupcevi3.Xekqes.domain.model.LeaderboardEntry

@Composable
fun LeaderboardScreen(
    onMenu: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val leaderboardViewModel: LeaderboardViewModel = viewModel(factory = ServiceLocator.factory)
    val state by leaderboardViewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        leaderboardViewModel.reload()
        onDispose { }
    }

    val visible = when (state.filter) {
        LeaderboardFilter.ALL_TIME -> state.entries
        LeaderboardFilter.TODAY -> state.entries.filter { it.epochDay == state.today }
    }

    ScreenBackground(
        overlay = listOf(
            Color(0xE616213E),
            Color(0xF20A0A0F)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectionHeader(
                    title = stringResource(R.string.leaderboard),
                    caption = stringResource(R.string.leaderboard_caption)
                )
                SecondaryButton(
                    label = stringResource(R.string.menu),
                    onClick = onMenu,
                    accent = GlassBorder,
                    leadingIcon = R.drawable.icon_home,
                    iconDescription = stringResource(R.string.cd_home)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SegmentedSelector(
                options = listOf(
                    stringResource(R.string.filter_all),
                    stringResource(R.string.filter_today)
                ),
                selectedIndex = state.filter.ordinal,
                onSelect = { index ->
                    leaderboardViewModel.select(LeaderboardFilter.entries[index])
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (visible.isEmpty()) {
                EmptyRoutes(onPlay = onPlay, modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(visible) { entry ->
                        LeaderboardRow(
                            entry = entry,
                            rank = visible.indexOf(entry) + 1,
                            today = state.today
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRoutes(
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.empty_body),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        PrimaryButton(
            label = stringResource(R.string.play),
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
private fun LeaderboardRow(
    entry: LeaderboardEntry,
    rank: Int,
    today: Long
) {
    val daysAgo = (today - entry.epochDay).toInt()
    val dayLabel = when {
        daysAgo <= 0 -> stringResource(R.string.day_today)
        daysAgo == 1 -> stringResource(R.string.day_yesterday)
        else -> stringResource(R.string.day_ago, daysAgo)
    }
    val rowState = stringResource(R.string.row_state, rank, Formatting.score(entry.score))
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { stateDescription = rowState },
        shape = RoundedCornerShape(14.dp),
        color = SurfaceRaised.copy(alpha = if (entry.own) 0.88f else 0.62f),
        border = BorderStroke(
            width = 1.dp,
            color = if (entry.own) ScarletFeather.copy(alpha = 0.70f) else GlassBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = if (rank <= 3) LaneAmber.copy(alpha = 0.22f) else Color(0x33FFFFFF),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
                    color = if (rank <= 3) LaneAmber else TextSecondary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = TextPrimary
                )
                Text(
                    text = dayLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = TextMuted
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formatting.score(entry.score),
                    style = ScoreStyle.copy(fontSize = 20.sp),
                    color = LaneAmber
                )
                Text(
                    text = Formatting.ratio(entry.lanes, entry.nearMisses),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = TextMuted
                )
            }
            if (entry.own) {
                Text(
                    text = stringResource(R.string.tag_new),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = ScarletFeather
                )
            }
        }
    }
}
