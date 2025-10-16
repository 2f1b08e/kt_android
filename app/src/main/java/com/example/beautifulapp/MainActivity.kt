package com.example.beautifulapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.yourappname.ui.theme.YourAppTheme // 👈 Make sure this matches your theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAppTheme {
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

    // ✨ NEW: Generate the bitmap icon programmatically ✨
    // This `remember` block recalculates the bitmap only when `isSelected` changes.
    val buttonBitmap by remember(isSelected) {
        mutableStateOf(createButtonIcon(context, isSelected))
    }

    Image(
        // Use the generated bitmap
        bitmap = buttonBitmap.asImageBitmap(),
        contentDescription = "Toggle Button",
        modifier = Modifier
            .size(80.dp)
            .scale(scale.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (scale.isRunning) return@clickable

                scope.launch {
                    scale.animateTo(targetValue = 0f, animationSpec = tween(150))
                    isSelected = !isSelected // State change triggers bitmap recalculation
                    scale.animateTo(targetValue = 1f, animationSpec = tween(150))
                }
            }
    )
}

/**
 * Recreates the icon drawing logic from the original Java code.
 */
private fun createButtonIcon(context: Context, isSelected: Boolean): Bitmap {
    val sizePx = (80 * context.resources.displayMetrics.density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Get the base drawable (you still need one!)
    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_magic_stick)
    drawable?.let {
        // Tint the drawable white
        DrawableCompat.setTint(it, android.graphics.Color.WHITE)
        DrawableCompat.setTintMode(it, PorterDuff.Mode.SRC_ATOP)

        // Center the drawable inside the bitmap (60% of total size)
        val iconSize = (sizePx * 0.6f).toInt()
        val left = (sizePx - iconSize) / 2
        val top = (sizePx - iconSize) / 2
        it.setBounds(left, top, left + iconSize, top + iconSize)
        it.draw(canvas)
    }

    // If selected, "punch a hole" in the icon
    if (isSelected) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        }
        val circleRadius = sizePx * 0.4f // 80% diameter -> 40% radius
        canvas.drawCircle(sizePx / 2f, sizePx / 2f, circleRadius, paint)
    }

    return bitmap
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YourAppTheme {
        ScreenContent()
    }
}
