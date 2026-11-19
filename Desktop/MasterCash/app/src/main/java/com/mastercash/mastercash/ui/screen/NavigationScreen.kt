package com.mastercash.mastercash.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.navigation.ComposeNavigator
import androidx.compose.navigation.NavHost
import androidx.compose.navigation.composable
import androidx.compose.navigation.rememberNavController
import com.mastercash.mastercash.ui.screen.DashboardScreen
import com.mastercash.mastercash.ui.screen.FormScreen
import com.mastercash.mastercash.ui.screen.GraficosScreen

@Composable
fun NavigationScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = fillMaxSize()
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            Modifier.fillMaxSize()
        ) {
            composable("dashboard") {
                DashboardScreen(navController, Modifier.padding(padding))
            }
            composable("form") {
                FormScreen(navController)
            }
            composable("edit/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                FormScreen(navController, editId = id)
            }
            composable("graficos") {
                GraficosScreen(navController, Modifier.padding(padding))
            }
        }
    }
}
