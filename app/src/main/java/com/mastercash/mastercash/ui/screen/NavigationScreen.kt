package com.mastercash.mastercash.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable("dashboard") {
                DashboardScreen(navController, Modifier)
            }
            composable("form") {
                FormScreen(navController)
            }
            composable("edit/{id}") { backStackEntry ->
                val idString = backStackEntry.arguments?.getString("id")
                val id = idString?.toIntOrNull() ?: -1
                FormScreen(navController, editId = id)
            }
            composable("graficos") {
                GraficosScreen(navController, Modifier)
            }
        }
    }
}
