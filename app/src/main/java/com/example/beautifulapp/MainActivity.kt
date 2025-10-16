package com.example.beautifulapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pageranimation.ui.theme.PagerAnimationTheme
import kotlin.math.absoluteValue

// A simple data class to hold the information for each page
data class PageData(
    val iconRes: Int,
    val title: String,
    val description: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Sample data for our pager pages.
        // Make sure you have these drawables in your res/drawable folder!
        val pages = listOf(
            PageData(R.drawable.ic_camera, "Telegram", "Use the Telegram's built-in camera."),
            PageData(R.drawable.ic_cherry, "CameraX", "CameraX is only supported on selected devices."),
            PageData(R.drawable.ic_google, "Camera 2", "All features are hidden when Camera2 is enabled."),
            PageData(R.drawable.ic_system, "System", "Access directly the system camera.")
        )

        setContent {
            PagerAnimationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraTypePager(pages)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CameraTypePager(pages: List<PageData>) {
    // 1. Remember the state of the pager (which page is selected, scroll position, etc.)
    val pagerState = rememberPagerState(pageCount = { pages.size })

    // 2. The main layout for our screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // This HorizontalPager is the core component
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { pageIndex ->
            // For each item in our pages list, we create a PagerItem
            PagerItem(
                pageData = pages[pageIndex],
                pagerState = pagerState,
                thisPageIndex = pageIndex
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerItem(pageData: PageData, pagerState: PagerState, thisPageIndex: Int) {
    
    // THE ANIMATION MAGIC 
    // 3. Calculate the offset of this page from the center.
    //    - When a page is centered, offset is 0.0
    //    - When a page is one full swipe to the left, offset is -1.0
    //    - When a page is one full swipe to the right, offset is 1.0
    val pageOffset = (pagerState.currentPage - thisPageIndex) + pagerState.currentPageOffsetFraction
    
    // 4. Calculate animation values based on the offset.
    //    We use absoluteValue so the effect is symmetrical whether swiping left or right.
    val alpha = 1.0f - pageOffset.absoluteValue.coerceIn(0f, 1f)
    val scale = 1.0f - (pageOffset.absoluteValue.coerceIn(0f, 1f) * 0.3f) // Shrink up to 30%

    // 5. The layout for a single page item
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Apply the animation values to the icon using a graphicsLayer
        Image(
            painter = painterResource(id = pageData.iconRes),
            contentDescription = pageData.title,
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scale
                    this.scaleY = scale
                }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = pageData.title,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = pageData.description,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PagerAnimationTheme {
        // Dummy data for the preview
        val pages = listOf(
            PageData(R.drawable.ic_camera, "Telegram", "Use the Telegram's built-in camera."),
            PageData(R.drawable.ic_cherry, "CameraX", "CameraX is only supported on selected devices.")
        )
        CameraTypePager(pages)
    }
}

// NOTE: You'll need to add some sample icons to your `res/drawable` folder.
// For example, you can create new Vector Assets in Android Studio and name them:
// ic_camera.xml
// ic_cherry.xml
// ic_google.xml
// ic_system.xml
