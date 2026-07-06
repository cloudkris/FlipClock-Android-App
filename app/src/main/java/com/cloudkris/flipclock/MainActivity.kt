package com.cloudkris.flipclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = Color.Black, modifier = Modifier.fillMaxSize()) {
                    FlipClockScreen()
                }
            }
        }
    }
}

private val cardBg = Color.White
private val cardText = Color.Black

@Composable
fun FlipClockScreen() {
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var amPm by remember { mutableStateOf("") }
    var dayName by remember { mutableStateOf("") }
    var dateNum by remember { mutableStateOf("") }
    var monthName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            val h24 = cal.get(Calendar.HOUR_OF_DAY)
            var h12 = h24 % 12
            if (h12 == 0) h12 = 12
            hour = if (h12 < 10) "0$h12" else "$h12"
            amPm = if (h24 >= 12) "pm" else "am"
            minute = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))
            dateNum = String.format(Locale.US, "%02d", cal.get(Calendar.DAY_OF_MONTH))
            dayName = SimpleDateFormat("EEEE", Locale.US).format(cal.time).uppercase()
            monthName = SimpleDateFormat("MMM", Locale.US).format(cal.time).uppercase()
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                FlipCard(value = hour, modifier = Modifier.fillMaxWidth().aspectRatio(1f))
                Text(
                    text = amPm,
                    color = cardText,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                )
            }
            FlipCard(value = minute, modifier = Modifier.weight(1f).aspectRatio(1f))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(cardBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayName,
                color = cardText,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FlipCard(value = dateNum, modifier = Modifier.weight(1f).aspectRatio(1.2f))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(cardBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = monthName,
                    color = cardText,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * A split-flap style card. Renders [value] and animates a top-half
 * rotation flip whenever the value changes, mimicking a mechanical
 * flip clock leaf turning over.
 */
@Composable
fun FlipCard(value: String, modifier: Modifier = Modifier) {
    var displayed by remember { mutableStateOf(value) }
    var previous by remember { mutableStateOf(value) }
    val rotation = remember { Animatable(180f) }

    LaunchedEffect(value) {
        if (value != displayed) {
            previous = displayed
            displayed = value
            rotation.snapTo(180f)
            rotation.animateTo(0f, animationSpec = tween(durationMillis = 350))
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(cardBg)
    ) {
        // Static bottom half of the new value (always visible under the flap)
        HalfDigit(text = displayed, top = false, modifier = Modifier.fillMaxSize())
        // Static top half of the new value (revealed as the flap opens)
        HalfDigit(text = displayed, top = true, modifier = Modifier.fillMaxSize())

        // Animated flap: shows old value's top half, then rotates down
        // through 90 degrees (edge-on) to reveal the new value.
        val angle = rotation.value
        if (angle > 90f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0f)
                        rotationX = angle
                        cameraDistance = 24f
                    }
            ) {
                HalfDigit(text = previous, top = true, modifier = Modifier.fillMaxSize())
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        rotationX = -180f + angle
                        cameraDistance = 24f
                    }
            ) {
                HalfDigit(text = displayed, top = false, modifier = Modifier.fillMaxSize())
            }
        }

        // Center divider line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.5f))
        )
    }
}

@Composable
private fun HalfDigit(text: String, top: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(HalfClip(top))) {
        Box(modifier = Modifier.fillMaxSize().background(cardBg), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = cardText,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private class HalfClip(private val top: Boolean) : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        val half = size.height / 2f
        val rect = if (top) {
            androidx.compose.ui.geometry.Rect(Offset(0f, 0f), Size(size.width, half))
        } else {
            androidx.compose.ui.geometry.Rect(Offset(0f, half), Size(size.width, half))
        }
        return androidx.compose.ui.graphics.Outline.Rectangle(rect)
    }
}
