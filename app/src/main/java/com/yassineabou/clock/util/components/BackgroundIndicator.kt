package com.yassineabou.clock.util.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.yassineabou.clock.ui.theme.ClockTheme
import com.yassineabou.clock.ui.theme.PrimaryLightAlpha
import com.yassineabou.clock.ui.theme.Red100
import kotlinx.coroutines.delay

@Composable
fun BackgroundIndicator(
    /*@FloatRange(from = 0.0, to = 1.0)*/
    progress: Float,
    signalTrigger: Int = 0,
    signalColor: Color = Red100,
    modifier: Modifier = Modifier,
    foregroundColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = PrimaryLightAlpha),
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
) {
    var isSignaling by remember { mutableStateOf(false) }

    LaunchedEffect(signalTrigger) {
        if (signalTrigger > 0) {
            isSignaling = true
            delay(500L)
            isSignaling = false
        }
    }

    val animatedForegroundColor by animateColorAsState(
        targetValue = if (isSignaling) signalColor else foregroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "foregroundColorAnimation"
    )

    Box(modifier) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            color = animatedForegroundColor,
            strokeWidth = strokeWidth,
        )
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor,
            strokeWidth = strokeWidth,
        )
    }
}

@Preview(
    name = "Light mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ScreenContentPreview() {
    ClockTheme {
        Surface {
            BackgroundIndicator(progress = .6f)
        }
    }
}