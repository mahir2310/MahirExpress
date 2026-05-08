package com.example.mahirexpress.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mahirexpress.model.Route
import com.example.mahirexpress.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListScreen(
    source: String,
    destination: String,
    date: String,
    onRouteClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RouteViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.searchRoutes(source, destination)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$source to $destination", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.routes) { route ->
                        BusRouteItem(route = route, onClick = { onRouteClick(route.routeId) })
                    }
                }
            }
        }
    }
}

@Composable
fun BusRouteItem(route: Route, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mahir Express - ${route.routeId.take(5)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${route.departureTime} - ${route.arrivalTime}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Available Seats: ${route.availableSeats}",
                    fontSize = 12.sp,
                    color = if (route.availableSeats > 5) Color(0xFF4CAF50) else Color.Red
                )
            }
            Text(
                text = "$${route.fare}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
