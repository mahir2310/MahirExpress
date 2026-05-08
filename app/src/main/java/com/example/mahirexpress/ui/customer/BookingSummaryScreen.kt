package com.example.mahirexpress.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mahirexpress.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSummaryScreen(
    routeId: String,
    seats: List<String>,
    totalFare: Double,
    name: String,
    email: String,
    phone: String,
    idNumber: String,
    onBookingSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            onBookingSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Summary") },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF121212))
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            SummarySection(title = "Journey Details") {
                SummaryRow(label = "Route ID", value = routeId)
                SummaryRow(label = "Seats", value = seats.joinToString(", "))
                SummaryRow(label = "Date", value = "TBD") // Would pass date in real flow
            }

            Spacer(modifier = Modifier.height(16.dp))

            SummarySection(title = "Passenger Details") {
                SummaryRow(label = "Name", value = name)
                SummaryRow(label = "Email", value = email)
                SummaryRow(label = "Phone", value = phone)
                SummaryRow(label = "ID", value = idNumber)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SummarySection(title = "Payment Details") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Fare", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "$$totalFare", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        viewModel.confirmBooking(
                            routeId = routeId,
                            busId = "B001", // Placeholder
                            seats = seats,
                            passengerName = name,
                            passengerEmail = email,
                            passengerPhone = phone,
                            totalAmount = totalFare,
                            journeyDate = "TBD"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Pay & Confirm Booking", fontWeight = FontWeight.Bold)
                }
            }

            viewModel.errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun SummarySection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray)
            content()
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.LightGray, fontSize = 14.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
