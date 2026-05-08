package com.example.mahirexpress.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahirexpress.util.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: (String, String, String) -> Unit,
    onMyBookingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    val userData = preferenceManager.getUserData()
    val userName = userData["name"] ?: "User"

    var source by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var travelDate by remember { mutableStateOf("Select Date") }
    
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        travelDate = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark Background
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome, $userName!",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Where would you like to go today?",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick = onProfileClick) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Card
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.LightGray,
                    cursorColor = Color.White
                )

                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("From") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    singleLine = true,
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("To") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                    singleLine = true,
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = travelDate)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { 
                        if (source.isNotBlank() && destination.isNotBlank() && travelDate != "Select Date") {
                            onSearchClick(source, destination, travelDate)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search Buses", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                onClick = onMyBookingsClick,
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("My Bookings", color = Color.White, fontSize = 14.sp)
                }
            }
            
            // Placeholder for another action (e.g., Offers or Settings)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Offers", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = {
                preferenceManager.clearSession()
                onLogout()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
        ) {
            Text("Logout", color = MaterialTheme.colorScheme.error)
        }
    }
}
