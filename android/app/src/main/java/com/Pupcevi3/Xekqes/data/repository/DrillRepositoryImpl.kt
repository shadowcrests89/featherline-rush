package com.Pupcevi3.Xekqes.data.repository

import com.Pupcevi3.Xekqes.core.config.GameConfig
import com.Pupcevi3.Xekqes.data.local.GamePreferences
import com.Pupcevi3.Xekqes.data.sample.SampleData
import com.Pupcevi3.Xekqes.domain.model.DailyDrill
import com.Pupcevi3.Xekqes.domain.model.DrillKind
import com.Pupcevi3.Xekqes.domain.model.RunResult
import com.Pupcevi3.Xekqes.domain.repository.DrillRepository

class DrillRepositoryImpl(private val store: GamePreferences) : DrillRepository {

    override fun drills(): List<DailyDrill> {
        val today = today()
        if (store.readLong(GamePreferences.KEY_DRILL_DAY, -1L) != today) {
            val fresh = SampleData.drillsForDay(today)
            store.writeLong(GamePreferences.KEY_DRILL_DAY, today)
            store.writeText(GamePreferences.KEY_DRILL_ROWS, encode(fresh))
            return fresh
        }
        val stored = decode(store.readText(GamePreferences.KEY_DRILL_ROWS, ""))
        return if (stored.isEmpty()) SampleData.drillsForDay(today) else stored
    }

    override fun applyRun(result: RunResult, burstsSpent: Int) {
        val updated = drills().map { drill ->
            if (drill.claimed) return@map drill
            val reached = when (drill.kind) {
                DrillKind.LANES -> result.lanes
                DrillKind.NEAR_MISSES -> result.nearMisses
                DrillKind.PARCELS -> result.parcels
                DrillKind.CLEAN_RUN -> if (burstsSpent == 0) result.lanes else 0
            }
            if (reached > drill.progress) drill.copy(progress = reached.coerceAtMost(drill.target)) else drill
        }
        store.writeText(GamePreferences.KEY_DRILL_ROWS, encode(updated))
    }

    override fun claim(kind: DrillKind) {
        val rows = drills()
        val target = rows.firstOrNull { it.kind == kind } ?: return
        if (!target.complete || target.claimed) return
        val updated = rows.map { if (it.kind == kind) it.copy(claimed = true) else it }
        store.writeText(GamePreferences.KEY_DRILL_ROWS, encode(updated))
        val cache = (featherCache() + 1).coerceAtMost(GameConfig.FEATHER_CACHE_CAP)
        store.writeInt(GamePreferences.KEY_FEATHER_CACHE, cache)
    }

    override fun featherCache(): Int {
        if (store.readLong(GamePreferences.KEY_DRILL_DAY, -1L) != today()) return 0
        return store.readInt(GamePreferences.KEY_FEATHER_CACHE, 0)
            .coerceIn(0, GameConfig.FEATHER_CACHE_CAP)
    }

    override fun minutesUntilReset(): Long {
        val elapsed = System.currentTimeMillis() % DAY_MS
        return (DAY_MS - elapsed) / 60_000L
    }

    private fun today(): Long = System.currentTimeMillis() / DAY_MS

    private fun encode(rows: List<DailyDrill>): String = rows.joinToString(ROW) { row ->
        listOf(
            row.kind.name,
            row.target.toString(),
            row.progress.toString(),
            if (row.claimed) "1" else "0"
        ).joinToString(FIELD)
    }

    private fun decode(raw: String): List<DailyDrill> {
        if (raw.isBlank()) return emptyList()
        return raw.split(ROW).mapNotNull { chunk ->
            val parts = chunk.split(FIELD)
            if (parts.size < 4) return@mapNotNull null
            val kind = DrillKind.entries.firstOrNull { it.name == parts[0] } ?: return@mapNotNull null
            DailyDrill(
                kind = kind,
                target = parts[1].toIntOrNull() ?: 1,
                progress = parts[2].toIntOrNull() ?: 0,
                claimed = parts[3] == "1"
            )
        }
    }

    private companion object {
        const val ROW = ";"
        const val FIELD = "|"
        const val DAY_MS = 86_400_000L
    }
}
