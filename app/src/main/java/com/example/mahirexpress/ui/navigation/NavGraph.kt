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
import com.example.mahirexpress.ui.customer.RouteListScreen
import com.example.mahirexpress.ui.customer.SeatSelectionScreen
import com.example.mahirexpress.ui.customer.PassengerInfoScreen
import com.example.mahirexpress.ui.customer.BookingSummaryScreen
import com.example.mahirexpress.ui.customer.MyBookingsScreen
import com.example.mahirexpress.ui.customer.ProfileScreen
import com.example.mahirexpress.util.PreferenceManager

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CustomerHome : Screen("customer_home")
    object AdminHome : Screen("admin_home")
    object ManagerHome : Screen("manager_home")
    object MyBookings : Screen("my_bookings")
    object Profile : Screen("profile")
    
    object RouteList : Screen("route_list/{source}/{destination}/{date}") {
        fun createRoute(source: String, destination: String, date: String) = 
            "route_list/$source/$destination/$date"
    }
    
    object SeatSelection : Screen("seat_selection/{routeId}") {
        fun createRoute(routeId: String) = "seat_selection/$routeId"
    }

    object PassengerInfo : Screen("passenger_info/{routeId}/{seats}/{fare}") {
        fun createRoute(routeId: String, seats: String, fare: Double) = 
            "passenger_info/$routeId/$seats/$fare"
    }

    object BookingSummary : Screen("booking_summary/{routeId}/{seats}/{fare}/{name}/{email}/{phone}/{id}") {
        fun createRoute(routeId: String, seats: String, fare: Double, name: String, email: String, phone: String, id: String) = 
            "booking_summary/$routeId/$seats/$fare/$name/$email/$phone/$id"
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
                onMyBookingsClick = {
                    navController.navigate(Screen.MyBookings.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MyBookings.route) {
            MyBookingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
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
            
            RouteListScreen(
                source = source,
                destination = dest,
                date = date,
                onRouteClick = { routeId ->
                    navController.navigate(Screen.SeatSelection.createRoute(routeId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SeatSelection.route) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            SeatSelectionScreen(
                routeId = routeId,
                onSeatConfirm = { selectedSeats, totalFare ->
                    val seatsStr = selectedSeats.joinToString(",")
                    navController.navigate(Screen.PassengerInfo.createRoute(routeId, seatsStr, totalFare))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PassengerInfo.route) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            val seats = backStackEntry.arguments?.getString("seats") ?: ""
            val fare = backStackEntry.arguments?.getString("fare")?.toDoubleOrNull() ?: 0.0
            
            PassengerInfoScreen(
                routeId = routeId,
                seats = seats.split(","),
                totalFare = fare,
                onConfirmBooking = { name, email, phone, id ->
                    navController.navigate(Screen.BookingSummary.createRoute(routeId, seats, fare, name, email, phone, id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BookingSummary.route) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            val seats = backStackEntry.arguments?.getString("seats") ?: ""
            val fare = backStackEntry.arguments?.getString("fare")?.toDoubleOrNull() ?: 0.0
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            val id = backStackEntry.arguments?.getString("id") ?: ""

            BookingSummaryScreen(
                routeId = routeId,
                seats = seats.split(","),
                totalFare = fare,
                name = name,
                email = email,
                phone = phone,
                idNumber = id,
                onBookingSuccess = {
                    navController.navigate(Screen.CustomerHome.route) {
                        popUpTo(Screen.CustomerHome.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminHome.route) {
            Text("Admin Dashboard - Implementation in Step 15")
        }
        composable(Screen.ManagerHome.route) {
            Text("Manager Dashboard - Implementation in Step 15")
        }
    }
}
