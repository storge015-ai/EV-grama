package com.example.testing.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.data.ChargingShop
import com.example.testing.ui.theme.TestingTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun EVGramaMapScreen(
    shops: List<ChargingShop>,
    travellerLocation: LatLng,
    hostLocation: LatLng,
    isUserTraveller: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onChargeNow: (ChargingShop) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(if (isUserTraveller) travellerLocation else hostLocation, 15f)
    }
    val travellerMarkerState = rememberMarkerState(position = travellerLocation)
    val hostMarkerState = rememberMarkerState(position = hostLocation)

    var selectedShop by remember { mutableStateOf<ChargingShop?>(null) }
    val scope = rememberCoroutineScope()

    // Keep markers in sync with live data
    LaunchedEffect(travellerLocation) {
        travellerMarkerState.position = travellerLocation
    }
    
    LaunchedEffect(hostLocation) {
        hostMarkerState.position = hostLocation
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (LocalInspectionMode.current) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE5E7EB)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Text("Live Google Map Placeholder", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("(Will render on Device)", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true),
                properties = MapProperties(isMyLocationEnabled = true)
            ) {
                // Current User Marker (Traveller)
                Marker(
                    state = travellerMarkerState,
                    title = "You",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Host Marker (If not traveller)
                if (!isUserTraveller) {
                    Marker(
                        state = hostMarkerState,
                        title = "Your Shop",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                    )
                }

                // Shop Markers
                for (shop in shops) {
                    MarkerInfoWindow(
                        state = rememberMarkerState(position = LatLng(shop.lat, shop.lng)),
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (shop.isAvailable) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED
                        ),
                        onClick = {
                            selectedShop = shop
                            scope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(LatLng(shop.lat, shop.lng), 16f),
                                    durationMs = 1000
                                )
                            }
                            false
                        }
                    ) {
                        Card(
                            modifier = Modifier.width(260.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF152222)),
                            border = BorderStroke(1.dp, Color(0xFF00DC7F).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(shop.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Price: ${shop.price}/hr", color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onChargeNow(shop) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F))
                                ) {
                                    Text("BOOK NOW", color = Color.Black, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // UI Overlays
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF10B981)) }
                Text("EV-Grama: Nearby Charging", color = Color.Gray, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Icon(Icons.Default.Search, null, tint = Color.Gray)
            }
        }

        FloatingActionButton(
            onClick = onNext,
            modifier = Modifier.padding(24.dp).align(Alignment.BottomEnd),
            containerColor = Color(0xFF00DC7F),
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }

        if (selectedShop != null) {
            val shop = selectedShop!!
            Card(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp).padding(bottom = 80.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1818)),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(shop.name, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(shop.distance, color = Color(0xFF10B981))
                                Text(" • ", color = Color.White.copy(alpha = 0.5f))
                                Text(shop.price, color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
                            }
                        }
                        IconButton(onClick = { selectedShop = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onChargeNow(shop) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CHARGE NOW", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
