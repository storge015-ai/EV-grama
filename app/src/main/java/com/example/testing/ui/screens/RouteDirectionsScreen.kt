package com.example.testing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testing.data.ChargingShop
import com.example.testing.ui.theme.TestingTheme
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun RouteDirectionsScreen(
    shop: ChargingShop,
    travellerLocation: LatLng,
    onBack: () -> Unit,
    onArrivedAtShop: () -> Unit
) {
    val hostLocation = LatLng(shop.lat, shop.lng)
    val cameraPositionState = rememberCameraPositionState {
        val lat = (travellerLocation.latitude + hostLocation.latitude) / 2
        val lng = (travellerLocation.longitude + hostLocation.longitude) / 2
        position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 12f)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF091010))) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (LocalInspectionMode.current) {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF152222)), contentAlignment = Alignment.Center) {
                    Text("Directions Map Preview", color = Color.Gray)
                }
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                ) {
                    Marker(
                        state = rememberMarkerState(position = travellerLocation),
                        title = "Your Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                    Marker(
                        state = rememberMarkerState(position = hostLocation),
                        title = shop.name,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                    Polyline(
                        points = listOf(travellerLocation, hostLocation),
                        color = Color(0xFFA020F0),
                        width = 10f
                    )
                }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(16.dp).align(Alignment.TopStart).background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF152222))
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Directions to ${shop.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Distance: ${shop.distance}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF00DC7F)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onArrivedAtShop,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Arrived at Shop", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RouteDirectionsScreenPreview() {
    TestingTheme {
        RouteDirectionsScreen(
            shop = ChargingShop("iTech Mobile Store", "0.5 km", "15A Fast", "+91 999 000 111", "₹50", 4.5f, 47.6128, -122.3214, isAvailable = true),
            travellerLocation = LatLng(47.6128, -122.3214),
            onBack = {},
            onArrivedAtShop = {}
        )
    }
}
