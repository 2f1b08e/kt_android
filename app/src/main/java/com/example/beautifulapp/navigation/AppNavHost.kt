package com.example.beautifulapp.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beautifulapp.ui.screens.HomeScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable("home") { HomeScreen() }
    }
}
