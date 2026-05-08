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
import com.example.mahirexpress.ui.customer.HomeScreen
import com.example.mahirexpress.util.PreferenceManager

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CustomerHome : Screen("customer_home")
    object AdminHome : Screen("admin_home")
    object ManagerHome : Screen("manager_home")
    object RouteList : Screen("route_list/{source}/{destination}/{date}") {
        fun createRoute(source: String, destination: String, date: String) = 
            "route_list/$source/$destination/$date"
    }
}

@Composable
fun MahirNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)
    
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

        composable(Screen.CustomerHome.route) {
            HomeScreen(
                onSearchClick = { source, dest, date ->
                    navController.navigate(Screen.RouteList.createRoute(source, dest, date))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RouteList.route) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: ""
            val dest = backStackEntry.arguments?.getString("destination") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            // Placeholder for now, will implement in Step 9
            Text("Listing routes from $source to $dest on $date")
        }

        composable(Screen.AdminHome.route) {
            Text("Admin Dashboard - Implementation in Step 15")
        }
        composable(Screen.ManagerHome.route) {
            Text("Manager Dashboard - Implementation in Step 15")
        }
    }
}
