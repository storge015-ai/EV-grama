package com.example.testing.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.ui.theme.TestingTheme

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
                    NumericStepperField(
                        label = "Seller Price (₹/hr)",
                        value = rate,
                        onValueChange = { rate = it },
                        placeholder = "Price",
                        icon = Icons.Default.CurrencyRupee
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    NumericStepperField(
                        label = "Total Number of Hours",
                        value = hours,
                        onValueChange = { hours = it },
                        placeholder = "Hours",
                        icon = Icons.Default.AccessTime
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    NumericStepperField(
                        label = "Vehicle Range per hour (km/hr)",
                        value = rangePerHour,
                        onValueChange = { rangePerHour = it },
                        placeholder = "Range",
                        icon = Icons.Default.ElectricScooter
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

@Composable
fun NumericStepperField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    step: Double = 1.0
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { 
                if (it.isEmpty() || it.toDoubleOrNull() != null || it == ".") {
                    onValueChange(it) 
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp) },
            leadingIcon = {
                IconButton(onClick = {
                    val current = value.toDoubleOrNull() ?: 0.0
                    val newValue = (current - step).coerceAtLeast(0.0)
                    onValueChange(if (newValue % 1.0 == 0.0) newValue.toInt().toString() else String.format("%.1f", newValue))
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color(0xFF00DC7F))
                }
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp)) {
                    Icon(icon, null, tint = Color.White.copy(alpha = 0.4f))
                    IconButton(onClick = {
                        val current = value.toDoubleOrNull() ?: 0.0
                        val newValue = current + step
                        onValueChange(if (newValue % 1.0 == 0.0) newValue.toInt().toString() else String.format("%.1f", newValue))
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color(0xFF00DC7F))
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F1818),
                unfocusedContainerColor = Color(0xFF0F1818),
                focusedBorderColor = Color(0xFF00DC7F),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF00DC7F),
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center, 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp,
                color = Color.White
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PayCalculatorScreenPreview() {
    TestingTheme {
        PayCalculatorScreen(onBack = {})
    }
}
