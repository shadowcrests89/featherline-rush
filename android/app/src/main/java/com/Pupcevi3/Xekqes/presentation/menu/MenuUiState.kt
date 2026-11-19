package com.Pupcevi3.Xekqes.presentation.menu

import com.Pupcevi3.Xekqes.domain.model.DailyDrill
import com.Pupcevi3.Xekqes.domain.model.DrillKind

data class MenuUiState(
    val bestScore: Int = 0,
    val bestLanes: Int = 0,
    val drills: List<DailyDrill> = emptyList(),
    val featherCache: Int = 0,
    val resetHours: Long = 0,
    val resetMinutes: Long = 0,
    val detail: DrillKind? = null,
    val justClaimed: DrillKind? = null
)
