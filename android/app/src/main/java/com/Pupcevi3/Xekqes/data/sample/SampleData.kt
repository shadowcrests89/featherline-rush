package com.Pupcevi3.Xekqes.data.sample

import com.Pupcevi3.Xekqes.domain.model.DailyDrill
import com.Pupcevi3.Xekqes.domain.model.DrillKind
import com.Pupcevi3.Xekqes.domain.model.LeaderboardEntry

object SampleData {

    const val OWN_LABEL = "YOUR ROUTE"

    fun seedLeaderboard(today: Long): List<LeaderboardEntry> = listOf(
        LeaderboardEntry("NIGHT COURIER", 1840, 46, 21, today - 1, false),
        LeaderboardEntry("RAIL RUNNER", 1420, 38, 17, today - 2, false),
        LeaderboardEntry("FERRY HOPPER", 1180, 31, 12, today - 3, false),
        LeaderboardEntry("AMBER WING", 940, 27, 9, today - 5, false),
        LeaderboardEntry("WET ASPHALT", 620, 19, 5, today - 8, false)
    )

    fun drillsForDay(day: Long): List<DailyDrill> {
        val pool = listOf(
            DailyDrill(DrillKind.LANES, 25, 0, false),
            DailyDrill(DrillKind.NEAR_MISSES, 6, 0, false),
            DailyDrill(DrillKind.PARCELS, 5, 0, false),
            DailyDrill(DrillKind.CLEAN_RUN, 15, 0, false)
        )
        val skip = (day % pool.size).toInt()
        return pool.filterIndexed { index, _ -> index != skip }
    }
}
