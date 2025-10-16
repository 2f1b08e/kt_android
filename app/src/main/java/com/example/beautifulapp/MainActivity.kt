package com.example.beautifulapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.beautifulapp.ui.theme.BeautifulAppTheme // Correct import for your theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // This uses the theme defined in the files below
            BeautifulAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenContent()
                }
            }
        }
    }
}

@Composable
fun ScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        EffectToggleButton()
    }
}

@Composable
fun EffectToggleButton() {
    var isSelected by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // This block generates the icon bitmap and re-runs only when `isSelected` changes.
    val buttonBitmap by remember(isSelected) {
        mutableStateOf(createButtonIcon(context, isSelected))
    }

    Image(
        bitmap = buttonBitmap.asImageBitmap(),
        contentDescription = "Toggle Button",
        modifier = Modifier
            .size(80.dp)
            .scale(scale.value) // The UI scale is bound to our animation value
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Prevent starting a new animation while one is running
                if (scale.isRunning) return@clickable

                // Launch the animation sequence in a coroutine
                scope.launch {
                    scale.animateTo(targetValue = 0f, animationSpec = tween(150))
                    isSelected = !isSelected // This state change triggers the bitmap to be redrawn
                    scale.animateTo(targetValue = 1f, animationSpec = tween(150))
                }
            }
    )
}

/**
 * Creates the button icon on a Bitmap using Canvas drawing.
 * This is the same logic as the original Cherrygram code.
 */
private fun createButtonIcon(context: Context, isSelected: Boolean): Bitmap {
    val sizePx = (80 * context.resources.displayMetrics.density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // You MUST provide one base icon named `ic_magic_stick.xml` in res/drawable
    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_magic_stick)
    drawable?.let {
        DrawableCompat.setTint(it, android.graphics.Color.WHITE)
        DrawableCompat.setTintMode(it, PorterDuff.Mode.SRC_ATOP)

        val iconSize = (sizePx * 0.6f).toInt()
        val left = (sizePx - iconSize) / 2
        val top = (sizePx - iconSize) / 2
        it.setBounds(left, top, left + iconSize, top + iconSize)
        it.draw(canvas)
    }

    // If selected, "punch a hole" out of the icon with a circle
    if (isSelected) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        }
        val circleRadius = sizePx * 0.4f
        canvas.drawCircle(sizePx / 2f, sizePx / 2f, circleRadius, paint)
    }

    return bitmap
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BeautifulAppTheme {
        ScreenContent()
    }
}


