package com.Pupcevi3.Xekqes.presentation.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.core.theme.DisabledBorder
import com.Pupcevi3.Xekqes.core.theme.DisabledText
import com.Pupcevi3.Xekqes.core.theme.GlassBorder
import com.Pupcevi3.Xekqes.core.theme.LaneAmber
import com.Pupcevi3.Xekqes.core.theme.ScarletFeather
import com.Pupcevi3.Xekqes.core.theme.SurfaceNight
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised
import com.Pupcevi3.Xekqes.core.theme.TextPrimary
import com.Pupcevi3.Xekqes.core.ui.PrimaryButton
import com.Pupcevi3.Xekqes.core.ui.ProgressTrack
import com.Pupcevi3.Xekqes.core.ui.SecondaryButton

@Composable
fun FloatingControlBar(
    state: GameUiState,
    onGo: () -> Unit,
    onHoldChange: (Boolean) -> Unit,
    onBurst: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceNight.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, GlassBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            MeterRow(state = state)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HoldPad(
                    active = state.holding,
                    onHoldChange = onHoldChange,
                    modifier = Modifier.width(92.dp)
                )
                PrimaryButton(
                    label = stringResource(R.string.go),
                    onClick = onGo,
                    enabled = state.phase != GamePhase.ENDED && !state.holding,
                    height = 56,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    label = stringResource(R.string.burst),
                    onClick = onBurst,
                    enabled = state.canBurst,
                    accent = ScarletFeather.copy(alpha = 0.60f),
                    modifier = Modifier.width(92.dp)
                )
            }
        }
    }
}

@Composable
private fun MeterRow(state: GameUiState) {
    val label = when {
        state.phase == GamePhase.RECOVERING -> stringResource(R.string.recovery_label)
        state.driftLeftMs > 0L -> stringResource(R.string.drift_label)
        state.blockedFlashMs > 0L -> stringResource(R.string.no_gap)
        else -> stringResource(R.string.slipstream)
    }
    val progress = when {
        state.phase == GamePhase.RECOVERING ->
            state.recoveryLeftMs / state.recoveryTotalMs.toFloat()

        state.driftLeftMs > 0L -> state.driftLeftMs / 4000f
        state.holding -> state.gapMeter
        else -> state.slipstream / 5f
    }
    val accent = when {
        state.phase == GamePhase.RECOVERING -> ScarletFeather
        state.blockedFlashMs > 0L -> ScarletFeather
        else -> LaneAmber
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = accent
        )
        Box(modifier = Modifier.weight(1f)) {
            ProgressTrack(
                progress = progress,
                accent = accent,
                thickness = 3,
                animated = false
            )
        }
    }
}

@Composable
private fun HoldPad(
    active: Boolean,
    onHoldChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(R.string.hold)
    Box(
        modifier = modifier
            .height(56.dp)
            .background(
                color = if (active) LaneAmber.copy(alpha = 0.26f) else SurfaceRaised.copy(alpha = 0.70f),
                shape = RoundedCornerShape(14.dp)
            )
            .size(width = 92.dp, height = 56.dp)
            .semantics { stateDescription = label }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onHoldChange(true)
                        tryAwaitRelease()
                        onHoldChange(false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
            color = if (active) LaneAmber else TextPrimary
        )
    }
}
