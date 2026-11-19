package com.Pupcevi3.Xekqes.presentation.menu

import androidx.lifecycle.ViewModel
import com.Pupcevi3.Xekqes.domain.model.DrillKind
import com.Pupcevi3.Xekqes.domain.repository.DrillRepository
import com.Pupcevi3.Xekqes.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel(
    private val scoreRepository: ScoreRepository,
    private val drillRepository: DrillRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        reload()
    }

    fun reload() {
        val minutes = drillRepository.minutesUntilReset()
        _state.update {
            it.copy(
                bestScore = scoreRepository.bestScore(),
                bestLanes = scoreRepository.bestLanes(),
                drills = drillRepository.drills(),
                featherCache = drillRepository.featherCache(),
                resetHours = minutes / 60L,
                resetMinutes = minutes % 60L,
                justClaimed = null
            )
        }
    }

    fun claim(kind: DrillKind) {
        drillRepository.claim(kind)
        _state.update {
            it.copy(
                drills = drillRepository.drills(),
                featherCache = drillRepository.featherCache(),
                justClaimed = kind
            )
        }
    }

    fun openDetail(kind: DrillKind) {
        _state.update { it.copy(detail = kind) }
    }

    fun closeDetail() {
        _state.update { it.copy(detail = null) }
    }
}
