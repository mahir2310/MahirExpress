package com.example.mahirexpress.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mahirexpress.ui.auth.LoginScreen
import com.example.mahirexpress.ui.auth.RegisterScreen
import com.example.mahirexpress.util.PreferenceManager

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CustomerHome : Screen("customer_home")
    object AdminHome : Screen("admin_home")
    object ManagerHome : Screen("manager_home")
}

@Composable
fun MahirNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)
    
    // Determine start destination based on login status
    val startDestination = if (preferenceManager.isLoggedIn()) {
        val role = preferenceManager.getUserData()["role"]
        when (role) {
            "Admin" -> Screen.AdminHome.route
            "Manager" -> Screen.ManagerHome.route
            else -> Screen.CustomerHome.route
        }
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "Admin" -> Screen.AdminHome.route
                        "Manager" -> Screen.ManagerHome.route
                        else -> Screen.CustomerHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Placeholders for Home Dashboards (to be implemented in Step 8 & 15)
        composable(Screen.CustomerHome.route) {
            Text("Customer Dashboard - Coming in Step 8")
        }
        composable(Screen.AdminHome.route) {
            Text("Admin Dashboard - Coming in Step 15")
        }
        composable(Screen.ManagerHome.route) {
            Text("Manager Dashboard - Coming in Step 15")
        }
    }
}
