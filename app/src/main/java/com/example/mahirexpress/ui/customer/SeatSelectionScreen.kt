package com.example.mahirexpress.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahirexpress.ui.theme.SeatAvailable
import com.example.mahirexpress.ui.theme.SeatBooked
import com.example.mahirexpress.ui.theme.SeatSelected

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    routeId: String,
    onSeatConfirm: (List<String>, Double) -> Unit,
    onBack: () -> Unit
) {
    // Dummy seats for now (A1, A2, ..., J4)
    val totalSeats = (1..40).map { 
        val row = ('A' + (it - 1) / 4)
        val col = (it - 1) % 4 + 1
        "$row$col" 
    }
    
    val selectedSeats = remember { mutableStateListOf<String>() }
    val farePerSeat = 1200.0 // This should come from the Route object later

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Seats - $routeId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (selectedSeats.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${selectedSeats.size} Seats Selected",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Total: $${selectedSeats.size * farePerSeat}")
                        }
                        Button(onClick = { onSeatConfirm(selectedSeats.toList(), selectedSeats.size * farePerSeat) }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // Seat Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SeatLegendItem("Available", SeatAvailable)
                SeatLegendItem("Selected", SeatSelected)
                SeatLegendItem("Booked", SeatBooked)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bus Front Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("FRONT / DRIVER", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seats Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(totalSeats) { seat ->
                    val isSelected = selectedSeats.contains(seat)
                    
                    // Add aisle spacing after 2nd column
                    val paddingModifier = if (totalSeats.indexOf(seat) % 4 == 1) {
                        Modifier.padding(end = 24.dp)
                    } else Modifier

                    Card(
                        modifier = paddingModifier
                            .aspectRatio(1f)
                            .clickable {
                                if (isSelected) selectedSeats.remove(seat)
                                else selectedSeats.add(seat)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) SeatSelected else SeatAvailable
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.EventSeat, contentDescription = null, modifier = Modifier.size(20.dp))
                                Text(text = seat, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeatLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(16.dp).background(color, RoundedCornerShape(4.dp)))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp)
    }
}
