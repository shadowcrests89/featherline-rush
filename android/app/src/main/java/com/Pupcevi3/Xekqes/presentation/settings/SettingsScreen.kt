package com.Pupcevi3.Xekqes.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.core.di.ServiceLocator
import com.Pupcevi3.Xekqes.core.theme.DisabledText
import com.Pupcevi3.Xekqes.core.theme.GlassBorder
import com.Pupcevi3.Xekqes.core.theme.LaneAmber
import com.Pupcevi3.Xekqes.core.theme.ScarletFeather
import com.Pupcevi3.Xekqes.core.theme.SurfaceNight
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised
import com.Pupcevi3.Xekqes.core.theme.TextMuted
import com.Pupcevi3.Xekqes.core.theme.TextPrimary
import com.Pupcevi3.Xekqes.core.theme.TextSecondary
import com.Pupcevi3.Xekqes.core.ui.GlassPanel
import com.Pupcevi3.Xekqes.core.ui.ScreenBackground
import com.Pupcevi3.Xekqes.core.ui.SecondaryButton
import com.Pupcevi3.Xekqes.core.ui.SectionHeader
import com.Pupcevi3.Xekqes.core.ui.SegmentedSelector
import com.Pupcevi3.Xekqes.presentation.dialog.ResetSettingsDialog

@Composable
fun SettingsScreen(
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.factory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    val settings = state.settings

    ScreenBackground(
        overlay = listOf(
            Color(0xF212121F),
            Color(0xF70A0A0F)
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
                    title = stringResource(R.string.settings),
                    caption = stringResource(R.string.settings_caption)
                )
                SecondaryButton(
                    label = stringResource(R.string.menu),
                    onClick = onMenu,
                    accent = GlassBorder,
                    leadingIcon = R.drawable.icon_home,
                    iconDescription = stringResource(R.string.cd_home)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OptionCard(
                        title = stringResource(R.string.set_reaction),
                        body = stringResource(R.string.set_reaction_body)
                    ) {
                        SegmentedSelector(
                            options = listOf(
                                stringResource(R.string.opt_relaxed),
                                stringResource(R.string.opt_standard),
                                stringResource(R.string.opt_sharp)
                            ),
                            selectedIndex = settings.reaction.ordinal,
                            onSelect = settingsViewModel::selectReaction
                        )
                    }
                }
                item {
                    OptionCard(
                        title = stringResource(R.string.set_recovery),
                        body = stringResource(R.string.set_recovery_body)
                    ) {
                        SegmentedSelector(
                            options = listOf(
                                stringResource(R.string.opt_recovery_short),
                                stringResource(R.string.opt_recovery_medium),
                                stringResource(R.string.opt_recovery_long)
                            ),
                            selectedIndex = settings.recovery.ordinal,
                            onSelect = settingsViewModel::selectRecovery,
                            accent = ScarletFeather
                        )
                    }
                }
                item {
                    OptionCard(
                        title = stringResource(R.string.set_hold_assist),
                        body = stringResource(R.string.set_hold_assist_body)
                    ) {
                        SwitchRow(
                            checked = settings.holdAssist,
                            onToggle = settingsViewModel::toggleHoldAssist
                        )
                    }
                }
                item {
                    OptionCard(
                        title = stringResource(R.string.set_feathers),
                        body = stringResource(R.string.set_feathers_body)
                    ) {
                        StepperRow(
                            value = settings.startingFeathers,
                            onChange = settingsViewModel::changeFeathers
                        )
                    }
                }
                item {
                    OptionCard(
                        title = stringResource(R.string.set_reduce_motion),
                        body = stringResource(R.string.set_reduce_motion_body)
                    ) {
                        SwitchRow(
                            checked = settings.reduceMotion,
                            onToggle = settingsViewModel::toggleReduceMotion
                        )
                    }
                }
                item {
                    OptionCard(
                        title = stringResource(R.string.set_contrast),
                        body = stringResource(R.string.set_contrast_body)
                    ) {
                        SwitchRow(
                            checked = settings.highContrast,
                            onToggle = settingsViewModel::toggleHighContrast
                        )
                    }
                }
                item {
                    TextButton(
                        onClick = settingsViewModel::askReset,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.reset_defaults),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (state.confirmReset) {
        ResetSettingsDialog(
            onConfirm = settingsViewModel::confirmReset,
            onDismiss = settingsViewModel::dismissReset
        )
    }
}

@Composable
private fun OptionCard(
    title: String,
    body: String,
    content: @Composable () -> Unit
) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        container = SurfaceNight.copy(alpha = 0.86f),
        border = GlassBorder,
        contentPadding = 14.dp
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun SwitchRow(
    checked: Boolean,
    onToggle: () -> Unit
) {
    val label = if (checked) {
        stringResource(R.string.state_on)
    } else {
        stringResource(R.string.state_off)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .semantics { stateDescription = label },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = if (checked) LaneAmber else DisabledText
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = SurfaceNight,
                checkedTrackColor = LaneAmber,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceRaised
            )
        )
    }
}

@Composable
private fun StepperRow(
    value: Int,
    onChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StepperButton(
            label = stringResource(R.string.step_down),
            description = stringResource(R.string.cd_step_down),
            onClick = { onChange(-1) }
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = LaneAmber,
            modifier = Modifier.semantics { stateDescription = value.toString() }
        )
        StepperButton(
            label = stringResource(R.string.step_up),
            description = stringResource(R.string.cd_step_up),
            onClick = { onChange(1) }
        )
    }
}

@Composable
private fun StepperButton(
    label: String,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(SurfaceRaised.copy(alpha = 0.72f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .semantics { stateDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
    }
}
