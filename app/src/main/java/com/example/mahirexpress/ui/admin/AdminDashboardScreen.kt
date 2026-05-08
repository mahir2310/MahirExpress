package com.example.mahirexpress.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mahirexpress.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchAllBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF121212))
                .padding(16.dp)
        ) {
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Revenue",
                    value = "$${viewModel.totalRevenue}",
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFF4CAF50)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Bookings",
                    value = "${viewModel.allBookings.size}",
                    icon = Icons.Default.ConfirmationNumber,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Recent Transactions", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.allBookings) { booking ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                            ListItem(
                                headlineContent = { Text(booking.passengerName, color = Color.White) },
                                supportingContent = { Text("Route: ${booking.routeId} | Seats: ${booking.seatNumbers.joinToString()}", color = Color.Gray) },
                                trailingContent = { Text("$${booking.totalAmount}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
