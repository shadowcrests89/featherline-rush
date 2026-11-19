package com.Pupcevi3.Xekqes.presentation.settings

import com.Pupcevi3.Xekqes.domain.model.GameSettings

data class SettingsUiState(
    val settings: GameSettings = GameSettings.DEFAULT,
    val confirmReset: Boolean = false
)
