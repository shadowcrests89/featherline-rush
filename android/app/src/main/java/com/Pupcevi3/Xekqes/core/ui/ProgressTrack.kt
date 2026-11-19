package com.Pupcevi3.Xekqes.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised

@Composable
fun ProgressTrack(
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier,
    thickness: Int = 4,
    animated: Boolean = true
) {
    val target = progress.coerceIn(0f, 1f)
    val shown by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = if (animated) 360 else 0),
        label = "trackProgress"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness.dp)
            .background(SurfaceRaised, RoundedCornerShape(thickness.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(shown)
                .fillMaxHeight()
                .background(accent, RoundedCornerShape(thickness.dp))
        )
    }
}
