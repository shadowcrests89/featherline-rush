package com.Pupcevi3.Xekqes.presentation.gameover

import androidx.lifecycle.ViewModel
import com.Pupcevi3.Xekqes.domain.model.RunResult
import com.Pupcevi3.Xekqes.domain.repository.DrillRepository
import com.Pupcevi3.Xekqes.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameOverViewModel(
    private val scoreRepository: ScoreRepository,
    private val drillRepository: DrillRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    fun load(result: RunResult) {
        val rows = scoreRepository.leaderboard()
        val best = scoreRepository.bestScore()
        val drill = drillRepository.drills().firstOrNull { !it.claimed } ?: drillRepository.drills().firstOrNull()
        _state.update {
            it.copy(
                bestScore = best,
                isRecord = result.score > 0 && result.score >= best,
                totalEntries = rows.size,
                drill = drill
            )
        }
    }
}
