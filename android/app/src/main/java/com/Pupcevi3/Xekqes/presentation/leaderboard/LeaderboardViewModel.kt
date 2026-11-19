package com.Pupcevi3.Xekqes.presentation.leaderboard

import androidx.lifecycle.ViewModel
import com.Pupcevi3.Xekqes.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LeaderboardViewModel(
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardUiState())
    val state: StateFlow<LeaderboardUiState> = _state.asStateFlow()

    init {
        reload()
    }

    fun reload() {
        val today = scoreRepository.todayEpochDay()
        val rows = scoreRepository.leaderboard().sortedByDescending { it.score }
        _state.update { it.copy(entries = rows, today = today) }
    }

    fun select(filter: LeaderboardFilter) {
        _state.update { it.copy(filter = filter) }
    }
}
