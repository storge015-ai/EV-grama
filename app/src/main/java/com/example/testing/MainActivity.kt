package com.example.testing

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.data.AppScreen
import com.example.testing.data.ChargingShop
import com.example.testing.data.FirebaseRepository
import com.example.testing.ui.screens.*
import com.example.testing.ui.theme.TestingTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val database = Firebase.database
            database.getReference("connection_status").setValue(System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            TestingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Welcome) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var selectedShopForDirections by remember { mutableStateOf<ChargingShop?>(null) }
    var isUserTraveller by rememberSaveable { mutableStateOf(true) }

    val repository = remember { FirebaseRepository() }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        launcher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    val defaultLocation = LatLng(47.6128, -122.3214)
    var liveTravellerLocation by remember { mutableStateOf(defaultLocation) }
    var liveHostLocation by remember { mutableStateOf(LatLng(47.6158, -122.3234)) }

    val shops = remember {
        mutableStateListOf(
            ChargingShop("iTech Mobile Store", "0.5 km", "15A Fast", "+91 999 000 111", "₹50", 4.5f, 47.6128, -122.3214, isAvailable = true),
            ChargingShop("Vikas Stationary", "0.8 km", "5A", "+91 999 000 222", "₹30", 4.2f, 47.6148, -122.3234, isAvailable = false),
            ChargingShop("Fresh Mart Grocery", "1.2 km", "15A Fast", "+91 999 000 333", "₹40", 4.8f, 47.6108, -122.3194, isAvailable = true)
        )
    }

    LaunchedEffect(Unit) {
        repository.observeShops { updatedShops ->
            if (updatedShops.isNotEmpty()) {
                shops.clear()
                shops.addAll(updatedShops)
            }
        }
        repository.observeTravellerLocation { location -> liveTravellerLocation = location }
        repository.observeHostLocation { location -> liveHostLocation = location }
    }

    when (currentScreen) {
        AppScreen.Welcome -> WelcomeScreen(onNext = { currentScreen = AppScreen.Onboarding1 })

        AppScreen.Onboarding1 -> OnboardingScreen(
            title = "Find and located charging Stations Near you",
            subtitle = "Find available charging points easily and reserve your charging point",
            image = Icons.Default.Map,
            pageIndex = 0,
            onNext = { currentScreen = AppScreen.Onboarding2 },
            onSkip = { currentScreen = AppScreen.Landing }
        )

        AppScreen.Onboarding2 -> OnboardingScreen(
            title = "Book and pay at any time from anywhere",
            subtitle = "Reserve your slot as per your choice and then pay online to confirm booking",
            image = Icons.Default.Payment,
            pageIndex = 1,
            onNext = { currentScreen = AppScreen.Onboarding3 },
            onSkip = { currentScreen = AppScreen.Landing }
        )

        AppScreen.Onboarding3 -> OnboardingScreen(
            title = "Sit back and relax, let your EV charge",
            subtitle = "Reach the charging point per your slot and charge comfortably",
            image = Icons.Default.ElectricScooter,
            pageIndex = 2,
            onNext = { currentScreen = AppScreen.Landing },
            onSkip = { currentScreen = AppScreen.Landing },
            isLastPage = true
        )

        AppScreen.Landing -> LandingScreen(
            onLoginClick = { loginError = null; currentScreen = AppScreen.LoginDetails },
            onRegisterClick = { currentScreen = AppScreen.RegisterDetails },
            onBack = { currentScreen = AppScreen.Onboarding3 }
        )

        AppScreen.LoginDetails -> AuthDetailsScreen(
            isLoginMode = true,
            onSuccess = { currentScreen = AppScreen.UserSelection },
            onBack = { currentScreen = AppScreen.Landing },
            errorMessage = loginError
        )

        AppScreen.RegisterDetails -> AuthDetailsScreen(
            isLoginMode = false,
            onSuccess = { currentScreen = AppScreen.Landing },
            onBack = { currentScreen = AppScreen.Landing }
        )

        AppScreen.UserSelection -> UserSelectionScreen { isTraveller ->
            isUserTraveller = isTraveller
            currentScreen = if (isTraveller) AppScreen.TravellerDetails else AppScreen.HostDetails
        }

        AppScreen.HostDetails -> HostDetailsScreen(
            hostLocation = liveHostLocation,
            onConfirm = { newShop ->
                repository.addShop(newShop)
                if (!shops.any { it.name == newShop.name && it.contact == newShop.contact }) {
                    shops.add(newShop)
                }
                liveHostLocation = LatLng(newShop.lat, newShop.lng)
                currentScreen = AppScreen.HostSuccess
            },
            onBack = { currentScreen = AppScreen.UserSelection }
        )

        AppScreen.HostSuccess -> HostSuccessScreen(
            onDone = { currentScreen = AppScreen.TravellerDetails },
            onBack = { currentScreen = AppScreen.HostDetails }
        )

        AppScreen.TravellerDetails -> BuyerDetailsScreen(
            onConfirm = { currentScreen = AppScreen.EVGramaMap },
            onBack = { currentScreen = if (isUserTraveller) AppScreen.UserSelection else AppScreen.HostSuccess }
        )

        AppScreen.EVGramaMap -> EVGramaMapScreen(
            shops = shops,
            travellerLocation = liveTravellerLocation,
            hostLocation = liveHostLocation,
            isUserTraveller = isUserTraveller,
            onBack = { currentScreen = AppScreen.TravellerDetails },
            onNext = { currentScreen = AppScreen.ShopSelection },
            onChargeNow = { shop ->
                selectedShopForDirections = shop
                currentScreen = AppScreen.ShopSelection
            }
        )

        AppScreen.ShopSelection -> ShopSelectionScreen(
            shops = shops,
            onShopSelected = { shop ->
                selectedShopForDirections = shop
                currentScreen = AppScreen.Directions
            },
            onBack = { currentScreen = AppScreen.EVGramaMap }
        )

        AppScreen.Directions -> selectedShopForDirections?.let { shop ->
            RouteDirectionsScreen(
                shop = shop,
                travellerLocation = liveTravellerLocation,
                onBack = { currentScreen = AppScreen.ShopSelection },
                onArrivedAtShop = { currentScreen = AppScreen.PayCalculator }
            )
        }

        AppScreen.PayCalculator -> PayCalculatorScreen(
            initialRate = selectedShopForDirections?.price?.filter { it.isDigit() } ?: "10",
            onBack = { currentScreen = AppScreen.Directions }
        )

        AppScreen.Charging -> ChargingScreen(
            onLogout = { currentScreen = AppScreen.Welcome },
            onGoToPayCalculator = { currentScreen = AppScreen.PayCalculator }
        )

        AppScreen.ShopSuccess -> SellerSuccessScreen(onBack = { currentScreen = AppScreen.Landing })
    }
}

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

// --- Previews ---
@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    TestingTheme {
        AppNavigation()
    }
}
