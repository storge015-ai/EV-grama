package com.example.testing.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.ui.components.AuthInputField

@Composable
fun PayCalculatorScreen(initialRate: String = "10", onBack: () -> Unit) {
    var hours by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf(initialRate) }
    var rangePerHour by remember { mutableStateOf("40") } // km per hour of charge
    var totalAmount by remember { mutableStateOf<Double?>(null) }
    var totalDistance by remember { mutableStateOf<Double?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Pay Calculator",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF00DC7F),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF152222)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    AuthInputField(
                        label = "Seller Price (₹/hr)",
                        value = rate,
                        onValueChange = { rate = it },
                        placeholder = "Enter host's price",
                        trailingIcon = Icons.Default.CurrencyRupee,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    AuthInputField(
                        label = "Total Number of Hours",
                        value = hours,
                        onValueChange = { hours = it },
                        placeholder = "Enter number of hours",
                        trailingIcon = Icons.Default.AccessTime,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AuthInputField(
                        label = "Vehicle Range per hour (km/hr)",
                        value = rangePerHour,
                        onValueChange = { rangePerHour = it },
                        placeholder = "e.g. 40",
                        trailingIcon = Icons.Default.ElectricScooter,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    val h = hours.toDoubleOrNull() ?: 0.0
                    val r = rate.toDoubleOrNull() ?: 0.0
                    val speed = rangePerHour.toDoubleOrNull() ?: 0.0
                    totalAmount = h * r
                    totalDistance = h * speed
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("CALCULATE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            if (totalAmount != null) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Total Pay Result
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00DC7F).copy(alpha = 0.1f)),
                    border = BorderStroke(2.dp, Color(0xFF00DC7F))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "total pay amount by traveller for hosts",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "₹${String.format("%.2f", totalAmount)}",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color(0xFF00DC7F),
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Distance Result
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "total distance vehicle can travel after charge",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%.1f", totalDistance)} km",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { /* Handle Payment Navigation */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("PAY NOW", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PayCalculatorScreenPreview() {
    MaterialTheme {
        PayCalculatorScreen(onBack = {})
    }
}
