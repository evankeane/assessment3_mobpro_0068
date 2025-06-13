//package com.evankeane.assessment3.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.evankeane.assessment3.ui.theme.screen.HomeScreen
//import com.evankeane.assessment3.ui.theme.screen.MainScreen
//
//
//@Composable
//fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
//    NavHost(
//        navController = navController,
//        startDestination = Screen.Home.route
//    ) {
//        composable(route = Screen.Home.route) {
//            HomeScreen(navController)
//        }
//        composable(route = Screen.MyFilm.route) {
//            MainScreen(navController)
//        }
//    }
//}