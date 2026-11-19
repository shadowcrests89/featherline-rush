package com.Pupcevi3.Xekqes.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.Pupcevi3.Xekqes.core.theme.StatValueStyle
import com.Pupcevi3.Xekqes.core.theme.SurfaceRaised
import com.Pupcevi3.Xekqes.core.theme.TextMuted

@Composable
fun StatCard(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    description: String = label
) {
    if (value.isBlank() || value == "0") return
    Surface(
        modifier = modifier
            .height(74.dp)
            .semantics { contentDescription = description },
        shape = RoundedCornerShape(16.dp),
        color = SurfaceRaised.copy(alpha = 0.80f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.33f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                modifier = Modifier.size(8.dp),
                shape = CircleShape,
                color = accent
            ) {
                Column(modifier = Modifier.background(accent)) {}
            }
            Text(
                text = value,
                style = StatValueStyle,
                color = accent,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}
