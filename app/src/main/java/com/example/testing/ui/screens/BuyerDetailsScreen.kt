package com.example.testing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testing.ui.components.AuthInputField
import com.example.testing.ui.components.LogoHeader

@Composable
fun BuyerDetailsScreen(onConfirm: () -> Unit, onBack: () -> Unit) {
    var vehicleName by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var adapterConfig by remember { mutableStateOf("5A") }
    var currentPercentage by remember { mutableStateOf(50f) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                LogoHeader()
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    "Buyer Details",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00DC7F),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                AuthInputField(
                    label = "Vehicle Name",
                    value = vehicleName,
                    onValueChange = { vehicleName = it },
                    placeholder = "e.g. Tesla Model 3",
                    trailingIcon = Icons.Default.DirectionsCar
                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthInputField(
                    label = "Vehicle Number",
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it },
                    placeholder = "e.g. ABC 1234",
                    trailingIcon = Icons.Default.Numbers
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text("Adapter Configuration", color = Color.White, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("5A", "10A", "15A").forEach {
                        FilterChip(
                            selected = adapterConfig == it,
                            onClick = { adapterConfig = it },
                            label = { Text(it) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00DC7F),
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Current Charging Percentage: ${currentPercentage.toInt()}%", color = Color.White, modifier = Modifier.fillMaxWidth())
                Slider(
                    value = currentPercentage,
                    onValueChange = { currentPercentage = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF00DC7F), activeTrackColor = Color(0xFF00DC7F))
                )
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    val brush = Brush.linearGradient(listOf(Color(0xFF00C853), Color(0xFF00E676)))
                    Box(modifier = Modifier.fillMaxSize().background(brush, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text("CONFIRM", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BuyerDetailsScreenPreview() {
    MaterialTheme {
        BuyerDetailsScreen(onConfirm = {}, onBack = {})
    }
}
