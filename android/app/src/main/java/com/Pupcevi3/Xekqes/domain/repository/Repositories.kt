package com.Pupcevi3.Xekqes.domain.repository

import com.Pupcevi3.Xekqes.domain.model.DailyDrill
import com.Pupcevi3.Xekqes.domain.model.DrillKind
import com.Pupcevi3.Xekqes.domain.model.GameSettings
import com.Pupcevi3.Xekqes.domain.model.LeaderboardEntry
import com.Pupcevi3.Xekqes.domain.model.RunResult

interface ScoreRepository {
    fun bestScore(): Int
    fun bestLanes(): Int
    fun leaderboard(): List<LeaderboardEntry>
    fun submit(result: RunResult): Int
    fun todayEpochDay(): Long
}

interface SettingsRepository {
    fun load(): GameSettings
    fun save(settings: GameSettings)
}

interface DrillRepository {
    fun drills(): List<DailyDrill>
    fun applyRun(result: RunResult, burstsSpent: Int)
    fun claim(kind: DrillKind)
    fun featherCache(): Int
    fun minutesUntilReset(): Long
}
