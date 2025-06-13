package com.evankeane.assessment3.ui.theme.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evankeane.assessment3.navigation.Screen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val screens = listOf(
        Screen.Home,
        Screen.Main
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = screens)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Main.route) { MainScreen() }
        }
    }
}
