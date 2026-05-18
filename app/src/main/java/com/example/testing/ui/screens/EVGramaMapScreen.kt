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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.data.ChargingShop
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

    var selectedShop by remember { mutableStateOf<ChargingShop?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(travellerLocation) {
        travellerMarkerState.position = travellerLocation
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (LocalInspectionMode.current) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE5E7EB)), contentAlignment = Alignment.Center) {
                Text("Map Preview", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                Marker(
                    state = travellerMarkerState,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    onClick = { true }
                )

                for (shop in shops) {
                    MarkerInfoWindow(
                        state = rememberMarkerState(position = LatLng(shop.lat, shop.lng)),
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (shop.isAvailable) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED
                        ),
                        onClick = {
                            selectedShop = shop
                            if (isUserTraveller) {
                                scope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngZoom(LatLng(shop.lat, shop.lng), 17f),
                                        durationMs = 1500
                                    )
                                }
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
                                Text("Shop: ${shop.name}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Adaptor: ${shop.adapterType}", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("Price: ", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                                    Text("${shop.price}/hr", color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Text("Contact: ${shop.contact}", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                                Box(
                                    modifier = Modifier.fillMaxWidth().background(Color(0xFF00DC7F), RoundedCornerShape(8.dp)).padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Book now", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF10B981)) }
                Text("EV-Grama: Local Charging", color = Color.Gray, modifier = Modifier.weight(1f))
                Icon(Icons.Default.Search, null, tint = Color.Gray)
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier.padding(24.dp).align(Alignment.BottomEnd),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F)),
            shape = CircleShape,
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.Black)
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
                                Text(shop.distance, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF10B981))
                                Text(" • ", color = Color.White.copy(alpha = 0.5f))
                                Text(shop.adapterType, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFFFFFFF).copy(alpha = 0.7f))
                                Text(" • ", color = Color.White.copy(alpha = 0.5f))
                                Text(shop.price, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
                            }
                            Text("Contact: ${shop.contact}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                        }
                        IconButton(onClick = { selectedShop = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onChargeNow(shop) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Charge Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
