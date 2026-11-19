package com.Pupcevi3.Xekqes.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.core.theme.LaneAmber
import com.Pupcevi3.Xekqes.core.theme.SurfaceNight
import com.Pupcevi3.Xekqes.core.theme.TextMuted
import com.Pupcevi3.Xekqes.core.theme.TextPrimary
import com.Pupcevi3.Xekqes.core.theme.TextSecondary
import com.Pupcevi3.Xekqes.core.ui.Formatting
import com.Pupcevi3.Xekqes.core.ui.ProgressTrack
import com.Pupcevi3.Xekqes.core.ui.drillRule
import com.Pupcevi3.Xekqes.core.ui.drillTitle
import com.Pupcevi3.Xekqes.domain.model.DailyDrill

@Composable
fun DrillDetailDialog(
    drill: DailyDrill,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        shape = RoundedCornerShape(22.dp),
        containerColor = SurfaceNight,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = drillTitle(drill.kind),
                style = MaterialTheme.typography.titleMedium,
                color = LaneAmber
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = drillRule(drill.kind, drill.target),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                ProgressTrack(
                    progress = drill.fraction,
                    accent = LaneAmber,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = Formatting.ratio(drill.progress, drill.target),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(R.string.drill_reward),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text(
                    text = stringResource(R.string.close),
                    style = MaterialTheme.typography.labelSmall,
                    color = LaneAmber
                )
            }
        }
    )
}
