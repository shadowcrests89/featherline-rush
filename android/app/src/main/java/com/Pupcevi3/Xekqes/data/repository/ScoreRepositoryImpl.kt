package com.Pupcevi3.Xekqes.data.repository

import com.Pupcevi3.Xekqes.core.config.GameConfig
import com.Pupcevi3.Xekqes.data.local.GamePreferences
import com.Pupcevi3.Xekqes.data.sample.SampleData
import com.Pupcevi3.Xekqes.domain.model.LeaderboardEntry
import com.Pupcevi3.Xekqes.domain.model.RunResult
import com.Pupcevi3.Xekqes.domain.repository.ScoreRepository

class ScoreRepositoryImpl(private val store: GamePreferences) : ScoreRepository {

    override fun bestScore(): Int = store.readInt(GamePreferences.KEY_BEST_SCORE, 0)

    override fun bestLanes(): Int = store.readInt(GamePreferences.KEY_BEST_LANES, 0)

    override fun todayEpochDay(): Long = System.currentTimeMillis() / DAY_MS

    override fun leaderboard(): List<LeaderboardEntry> {
        if (!store.contains(GamePreferences.KEY_LEADERBOARD)) {
            val seeded = SampleData.seedLeaderboard(todayEpochDay())
            store.writeText(GamePreferences.KEY_LEADERBOARD, encode(seeded))
            return seeded
        }
        return decode(store.readText(GamePreferences.KEY_LEADERBOARD, ""))
    }

    override fun submit(result: RunResult): Int {
        if (result.score > bestScore()) store.writeInt(GamePreferences.KEY_BEST_SCORE, result.score)
        if (result.lanes > bestLanes()) store.writeInt(GamePreferences.KEY_BEST_LANES, result.lanes)
        val fresh = LeaderboardEntry(
            label = SampleData.OWN_LABEL,
            score = result.score,
            lanes = result.lanes,
            nearMisses = result.nearMisses,
            epochDay = todayEpochDay(),
            own = true
        )
        val merged = (leaderboard().map { it.copy(own = false) } + fresh)
            .sortedByDescending { it.score }
            .take(GameConfig.LEADERBOARD_SIZE)
        store.writeText(GamePreferences.KEY_LEADERBOARD, encode(merged))
        val rank = merged.indexOfFirst { it.own && it.score == result.score }
        return if (rank < 0) 0 else rank + 1
    }

    private fun encode(rows: List<LeaderboardEntry>): String = rows.joinToString(ROW) { row ->
        listOf(
            row.label,
            row.score.toString(),
            row.lanes.toString(),
            row.nearMisses.toString(),
            row.epochDay.toString(),
            if (row.own) "1" else "0"
        ).joinToString(FIELD)
    }

    private fun decode(raw: String): List<LeaderboardEntry> {
        if (raw.isBlank()) return emptyList()
        return raw.split(ROW).mapNotNull { chunk ->
            val parts = chunk.split(FIELD)
            if (parts.size < 6) return@mapNotNull null
            LeaderboardEntry(
                label = parts[0],
                score = parts[1].toIntOrNull() ?: 0,
                lanes = parts[2].toIntOrNull() ?: 0,
                nearMisses = parts[3].toIntOrNull() ?: 0,
                epochDay = parts[4].toLongOrNull() ?: 0L,
                own = parts[5] == "1"
            )
        }
    }

    private companion object {
        const val ROW = ";"
        const val FIELD = "|"
        const val DAY_MS = 86_400_000L
    }
}
