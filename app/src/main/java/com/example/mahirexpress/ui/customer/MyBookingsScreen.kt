package com.example.mahirexpress.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
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
import com.example.mahirexpress.model.Booking
import com.example.mahirexpress.viewmodel.MyBookingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onBack: () -> Unit,
    viewModel: MyBookingsViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchUserBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null && viewModel.bookings.isEmpty()) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.bookings) { booking ->
                        BookingItem(booking)
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ConfirmationNumber,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Booking ID: ${booking.bookingId.take(8).uppercase()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Seats: ${booking.seatNumbers.joinToString(", ")}",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Date: ${booking.journeyDate}",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
            Text(
                text = "$${booking.totalAmount}",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
    }
}
