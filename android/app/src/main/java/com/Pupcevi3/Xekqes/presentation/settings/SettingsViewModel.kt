package com.Pupcevi3.Xekqes.presentation.settings

import androidx.lifecycle.ViewModel
import com.Pupcevi3.Xekqes.domain.model.GameSettings
import com.Pupcevi3.Xekqes.domain.model.ReactionWindow
import com.Pupcevi3.Xekqes.domain.model.RecoveryWindow
import com.Pupcevi3.Xekqes.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        _state.update { it.copy(settings = settingsRepository.load()) }
    }

    fun selectReaction(index: Int) {
        apply(_state.value.settings.copy(reaction = ReactionWindow.entries[index]))
    }

    fun selectRecovery(index: Int) {
        apply(_state.value.settings.copy(recovery = RecoveryWindow.entries[index]))
    }

    fun toggleHoldAssist() {
        apply(_state.value.settings.copy(holdAssist = !_state.value.settings.holdAssist))
    }

    fun toggleReduceMotion() {
        apply(_state.value.settings.copy(reduceMotion = !_state.value.settings.reduceMotion))
    }

    fun toggleHighContrast() {
        apply(_state.value.settings.copy(highContrast = !_state.value.settings.highContrast))
    }

    fun changeFeathers(delta: Int) {
        val value = (_state.value.settings.startingFeathers + delta)
            .coerceIn(GameSettings.MIN_FEATHERS, GameSettings.MAX_START_FEATHERS)
        apply(_state.value.settings.copy(startingFeathers = value))
    }

    fun askReset() {
        _state.update { it.copy(confirmReset = true) }
    }

    fun dismissReset() {
        _state.update { it.copy(confirmReset = false) }
    }

    fun confirmReset() {
        settingsRepository.save(GameSettings.DEFAULT)
        _state.update { it.copy(settings = GameSettings.DEFAULT, confirmReset = false) }
    }

    private fun apply(settings: GameSettings) {
        settingsRepository.save(settings)
        _state.update { it.copy(settings = settings) }
    }
}
