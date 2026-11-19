package com.Pupcevi3.Xekqes.domain.usecase

import com.Pupcevi3.Xekqes.domain.model.RunResult
import com.Pupcevi3.Xekqes.domain.repository.DrillRepository
import com.Pupcevi3.Xekqes.domain.repository.ScoreRepository

class SubmitRunUseCase(
    private val scoreRepository: ScoreRepository,
    private val drillRepository: DrillRepository
) {
    operator fun invoke(result: RunResult, burstsSpent: Int): Int {
        drillRepository.applyRun(result, burstsSpent)
        return scoreRepository.submit(result)
    }
}
