package com.example.testing.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.data.ChargingShop
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun ShopSelectionScreen(
    shops: List<ChargingShop>,
    buyerLocation: LatLng = LatLng(47.6128, -122.3214), // Broadway & Madison, Seattle
    onShopSelected: () -> Unit,
    onBack: () -> Unit
) {
    // Added LocalInspectionMode to fix black map issue in Android Studio Preview
    val isInspectionMode = LocalInspectionMode.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(buyerLocation, 15f)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Google Map Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isInspectionMode) {
                    // Show a placeholder in Preview mode to avoid black screen and rendering issues
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF152222)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.FlashOn,
                                null,
                                tint = Color(0xFF00DC7F),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "Live Map Preview",
                                color = Color(0xFF00DC7F),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = false
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        )
                    ) {
                        // Buyer Marker (Red icon to match image)
                        Marker(
                            state = rememberMarkerState(position = buyerLocation),
                            title = "Your Location",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )

                        // Seller Markers
                        shops.forEach { shop ->
                            Marker(
                                state = rememberMarkerState(position = LatLng(shop.lat, shop.lng)),
                                title = shop.name,
                                snippet = "Adaptor: ${shop.adapterType} • ${shop.price}/hr",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            }

            // Shop List Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .background(Color(0xFF091010))
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Available Charging Slots",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00DC7F),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nearby locations matching your vehicle profile", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(shops) { shop ->
                        ShopCard(shop, onShopSelected)
                    }
                }
            }
        }
    }
}

@Composable
fun ShopCard(shop: ChargingShop, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF152222)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(shop.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                Text(shop.distance, color = Color(0xFF00DC7F), fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                Text(" ${shop.rating}", color = Color.White, fontSize = 14.sp)
                Text(" • Adaptor: ${shop.adapterType}", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                Text(" ${shop.contact}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${shop.price}/hr", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)

                Surface(
                    color = Color(0xFF00DC7F),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FlashOn, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("BOOK NOW", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShopSelectionScreenPreview() {
    // Explicitly providing all arguments to ChargingShop constructor to ensure class compatibility in Preview
    val mockShops = listOf(
        ChargingShop("Green Energy Hub", "0.8 km", "15A", "+91 999 888 777", "₹60", 4.9f, 47.6128, -122.3214, true),
        ChargingShop("Solar Power Station", "2.1 km", "10A", "+91 888 000 222", "₹45", 4.3f, 47.6158, -122.3244, true)
    )
    MaterialTheme {
        ShopSelectionScreen(shops = mockShops, onShopSelected = {}, onBack = {})
    }
}
