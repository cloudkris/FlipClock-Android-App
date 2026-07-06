package com.cloudkris.flipclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
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

private val cardBg = Color.Black
private val cardText = Color(0xFFF5F1E6)
private val cardOutline = Color(0xFF666666)
private val retroFont = FontFamily.Monospace
private val flapShape = RoundedCornerShape(6.dp)

@Composable
fun FlipClockScreen() {
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var second by remember { mutableStateOf("") }
    var dayName by remember { mutableStateOf("") }
    var dateNum by remember { mutableStateOf("") }
    var monthName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            hour = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
            minute = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))
            second = String.format(Locale.US, "%02d", cal.get(Calendar.SECOND))
            dateNum = String.format(Locale.US, "%02d", cal.get(Calendar.DAY_OF_MONTH))
            dayName = SimpleDateFormat("EEEE", Locale.US).format(cal.time).uppercase()
            monthName = SimpleDateFormat("MMM", Locale.US).format(cal.time).uppercase()
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FlipCard(
                value = hour,
                fontSize = 58.sp,
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
            FlipCard(
                value = minute,
                fontSize = 58.sp,
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
            FlipCard(
                value = second,
                fontSize = 58.sp,
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(90.dp)
                .clip(flapShape)
                .background(cardBg)
                .border(BorderStroke(2.dp, cardOutline), flapShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayName,
                color = cardText,
                fontSize = 38.sp,
                fontFamily = retroFont,
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
            FlipCard(
                value = dateNum,
                fontSize = 68.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.2f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.2f)
                    .clip(flapShape)
                    .background(cardBg)
                    .border(BorderStroke(2.dp, cardOutline), flapShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = monthName,
                    color = cardText,
                    fontSize = 46.sp,
                    fontFamily = retroFont,
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
private val flipEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

@Composable
fun FlipCard(value: String, fontSize: androidx.compose.ui.unit.TextUnit, modifier: Modifier = Modifier) {
    var displayed by remember { mutableStateOf(value) }
    var previous by remember { mutableStateOf(value) }
    val rotation = remember { Animatable(180f) }

    LaunchedEffect(value) {
        if (value != displayed) {
            previous = displayed
            displayed = value
            rotation.snapTo(180f)
            rotation.animateTo(0f, animationSpec = tween(durationMillis = 420, easing = flipEasing))
        }
    }

    Box(
        modifier = modifier
            .clip(flapShape)
            .background(cardBg)
            .border(BorderStroke(2.dp, cardOutline), flapShape)
    ) {
        // Static bottom half of the new value (always visible under the flap)
        HalfDigit(text = displayed, top = false, fontSize = fontSize, modifier = Modifier.fillMaxSize())
        // Static top half of the new value (revealed as the flap opens)
        HalfDigit(text = displayed, top = true, fontSize = fontSize, modifier = Modifier.fillMaxSize())

        // Animated flap: shows old value's top half, then rotates down
        // through 90 degrees (edge-on) to reveal the new value. The
        // easing above lets this slightly overshoot past flat before
        // settling, like a real leaf clacking into place.
        val angle = rotation.value
        if (angle > 90f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0f)
                        rotationX = angle
                        cameraDistance = 10f
                    }
            ) {
                HalfDigit(text = previous, top = true, fontSize = fontSize, modifier = Modifier.fillMaxSize())
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        rotationX = -180f + angle
                        cameraDistance = 10f
                    }
            ) {
                HalfDigit(text = displayed, top = false, fontSize = fontSize, modifier = Modifier.fillMaxSize())
            }
        }

        // Shadow that darkens the flap as it swings edge-on, like light
        // catching the fold of a real mechanical leaf mid-turn.
        val edgeShadow = (1f - (kotlin.math.abs(angle - 90f) / 90f)).coerceIn(0f, 1f) * 0.4f
        if (edgeShadow > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = edgeShadow))
            )
        }
    }
}

@Composable
private fun HalfDigit(
    text: String,
    top: Boolean,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clip(HalfClip(top))) {
        Box(modifier = Modifier.fillMaxSize().background(cardBg), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = cardText,
                fontSize = fontSize,
                fontFamily = retroFont,
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
