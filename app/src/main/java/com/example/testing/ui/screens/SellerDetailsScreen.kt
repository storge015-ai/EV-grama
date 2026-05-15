package com.example.testing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testing.data.ChargingShop
import com.example.testing.ui.components.AuthInputField
import com.example.testing.ui.components.LogoHeader

@Composable
fun SellerDetailsScreen(onConfirm: (ChargingShop) -> Unit, onBack: () -> Unit) {
    var shopName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var adapterType by remember { mutableStateOf("5A") }
    var pricePerHour by remember { mutableStateOf("") }

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
                    "Seller Details",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00DC7F),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                AuthInputField(
                    label = "Shop Name",
                    value = shopName,
                    onValueChange = { shopName = it },
                    placeholder = "e.g. Green Energy Hub",
                    trailingIcon = Icons.Default.Store
                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthInputField(
                    label = "Contact Number",
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    placeholder = "e.g. +91 999 000 111",
                    trailingIcon = Icons.Default.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthInputField(
                    label = "Location",
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "Enter shop address",
                    trailingIcon = Icons.Default.LocationOn
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text("Type of Adaptor", color = Color.White, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("5A", "10A", "15A").forEach {
                        FilterChip(
                            selected = adapterType == it,
                            onClick = { adapterType = it },
                            label = { Text(it) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00DC7F),
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                AuthInputField(
                    label = "Price Per Hour (₹)",
                    value = pricePerHour,
                    onValueChange = { pricePerHour = it },
                    placeholder = "e.g. 50",
                    trailingIcon = Icons.Default.CurrencyRupee,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)) {
                Button(
                    onClick = {
                        val newShop = ChargingShop(
                            name = shopName.ifBlank { "New Shop" },
                            distance = "0.5 km", // Mock distance
                            adapterType = adapterType,
                            contact = contactNumber,
                            price = "₹${pricePerHour.ifBlank { "0" }}",
                            rating = 5.0f,
                            lat = 37.7749 + (Math.random() * 0.1 - 0.05), // Mock coords
                            lng = -122.4194 + (Math.random() * 0.1 - 0.05)
                        )
                        onConfirm(newShop)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    val brush = Brush.linearGradient(listOf(Color(0xFF00C853), Color(0xFF00E676)))
                    Box(modifier = Modifier.fillMaxSize().background(brush, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text("START SELLING", color = Color.White, fontWeight = FontWeight.Bold)
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
fun SellerDetailsScreenPreview() {
    MaterialTheme {
        SellerDetailsScreen(onConfirm = {}, onBack = {})
    }
}
