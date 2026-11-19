package com.Pupcevi3.Xekqes.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.Pupcevi3.Xekqes.R
import com.Pupcevi3.Xekqes.domain.model.DrillKind
import com.Pupcevi3.Xekqes.domain.model.LaneKind

@Composable
fun drillTitle(kind: DrillKind): String = when (kind) {
    DrillKind.LANES -> stringResource(R.string.drill_lanes)
    DrillKind.NEAR_MISSES -> stringResource(R.string.drill_near)
    DrillKind.PARCELS -> stringResource(R.string.drill_parcels)
    DrillKind.CLEAN_RUN -> stringResource(R.string.drill_clean)
}

@Composable
fun drillRule(kind: DrillKind, target: Int): String = when (kind) {
    DrillKind.LANES -> stringResource(R.string.drill_lanes_rule, target)
    DrillKind.NEAR_MISSES -> stringResource(R.string.drill_near_rule, target)
    DrillKind.PARCELS -> stringResource(R.string.drill_parcels_rule, target)
    DrillKind.CLEAN_RUN -> stringResource(R.string.drill_clean_rule, target)
}

@Composable
fun laneLabel(kind: LaneKind): String = when (kind) {
    LaneKind.ROAD -> stringResource(R.string.lane_road)
    LaneKind.RAIL -> stringResource(R.string.lane_rail)
    LaneKind.RIVER -> stringResource(R.string.lane_river)
    LaneKind.KERB -> stringResource(R.string.lane_kerb)
}

fun laneIcon(kind: LaneKind): Int = when (kind) {
    LaneKind.ROAD -> R.drawable.ic_lane_road
    LaneKind.RAIL -> R.drawable.ic_lane_rail
    LaneKind.RIVER -> R.drawable.ic_lane_river
    LaneKind.KERB -> R.drawable.ic_lane_road
}
