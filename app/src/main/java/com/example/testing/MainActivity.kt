package com.example.testing

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testing.data.AppScreen
import com.example.testing.data.ChargingShop
import com.example.testing.data.FirebaseRepository
import com.example.testing.ui.screens.*
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

        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("message")
        myRef.setValue("Hello, World!")

        setContent {
            MaterialTheme {
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
    var showLocationConfirmDialog by remember { mutableStateOf(false) }
    
    // Initialize Realtime Database Repository
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

    // Default location (e.g. Seattle center)
    val defaultLocation = LatLng(47.6128, -122.3214)
    var liveTravellerLocation by remember { mutableStateOf(defaultLocation) }

    val shops = remember {
        mutableStateListOf(
            ChargingShop("iTech Mobile Store", "0.5 km", "15A", "+91 999 000 111", "Open", 4.5f, 47.6128, -122.3214, isAvailable = true),
            ChargingShop("Vikas Stationary", "0.8 km", "5A", "+91 999 000 222", "Open", 4.2f, 47.6148, -122.3234, isAvailable = false),
            ChargingShop("Fresh Mart Grocery", "1.2 km", "15A", "+91 999 000 333", "Open", 4.8f, 47.6108, -122.3194, isAvailable = true),
            ChargingShop("Auto Lubricants", "1.5 km", "5A", "+91 999 000 444", "Open", 4.0f, 47.6168, -122.3254, isAvailable = true),
            ChargingShop("Sweet Delights Bakery", "2.0 km", "15A", "+91 999 000 555", "Open", 4.7f, 47.6088, -122.3214, isAvailable = false),
            ChargingShop("Green View Hotel", "2.5 km", "5A", "+91 999 000 666", "₹1200", 4.3f, 47.6058, -122.3274, isAvailable = true)
        )
    }

    // Observe shops and traveller location from Realtime Database
    LaunchedEffect(Unit) {
        repository.observeShops { updatedShops ->
            if (updatedShops.isNotEmpty()) {
                shops.clear()
                shops.addAll(updatedShops)
            }
        }
        repository.observeTravellerLocation { location ->
            liveTravellerLocation = location
        }
    }

    // Live Location Permission Message
    if (showLocationConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLocationConfirmDialog = false },
            title = { Text("Allow Live Location") },
            text = { Text("To help you reach the host's shop easily, please allow access to your live location.") },
            confirmButton = {
                TextButton(onClick = {
                    showLocationConfirmDialog = false
                    currentScreen = AppScreen.EVGramaMap
                }) {
                    Text("ALLOW", color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationConfirmDialog = false }) {
                    Text("CANCEL", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF152222),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            shape = RoundedCornerShape(24.dp)
        )
    }

    when (currentScreen) {
        AppScreen.Welcome -> WelcomeScreen(
            onNext = { currentScreen = AppScreen.Onboarding1 }
        )
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
            onLoginClick = { 
                loginError = null
                currentScreen = AppScreen.LoginDetails 
            },
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
        AppScreen.UserSelection -> UserSelectionScreen(
            onRoleSelected = { isTraveller ->
                currentScreen = if (isTraveller) AppScreen.TravellerDetails else AppScreen.SellerDetails
            }
        )
        AppScreen.TravellerDetails -> TravellerDetailsScreen(
            onConfirm = { showLocationConfirmDialog = true },
            onBack = { currentScreen = AppScreen.UserSelection }
        )
        AppScreen.SellerDetails -> HostDetailsScreen(
            onConfirm = { newShop ->
                // Add to Realtime Database
                repository.addShop(newShop)
                currentScreen = AppScreen.SellerSuccess
            },
            onBack = { currentScreen = AppScreen.UserSelection }
        )
        AppScreen.SellerSuccess -> SellerSuccessScreen(
            onBack = { currentScreen = AppScreen.SellerDetails }
        )
        AppScreen.ShopSelection -> ShopSelectionScreen(
            shops = shops,
            travellerLocation = liveTravellerLocation,
            onShopSelected = { shop ->
                selectedShopForDirections = shop
                currentScreen = AppScreen.Charging
            },
            onBack = { currentScreen = AppScreen.TravellerDetails }
        )
        AppScreen.EVGramaMap -> EVGramaMapScreen(
            shops = shops,
            travellerLocation = liveTravellerLocation,
            onBack = { currentScreen = AppScreen.TravellerDetails },
            onChargeNow = { shop ->
                selectedShopForDirections = shop
                currentScreen = AppScreen.Directions
            },
            onUpdateLocation = { newLocation ->
                repository.updateTravellerLocation(newLocation)
            }
        )
        AppScreen.Charging -> ChargingScreen(
            onLogout = { currentScreen = AppScreen.Welcome },
            onGoToPayCalculator = { currentScreen = AppScreen.PayCalculator }
        )
        AppScreen.Directions -> selectedShopForDirections?.let { shop ->
            DirectionsScreen(
                shop = shop,
                travellerLocation = liveTravellerLocation,
                onBack = { currentScreen = AppScreen.EVGramaMap },
                onGoToPayCalculator = { currentScreen = AppScreen.PayCalculator }
            )
        }
        AppScreen.PayCalculator -> {
            val hostPrice = selectedShopForDirections?.price?.filter { it.isDigit() }.let { if (it.isNullOrEmpty()) "10" else it }
            PayCalculatorScreen(
                initialRate = hostPrice,
                onBack = { currentScreen = AppScreen.Charging }
            )
        }
    }
}

// --- SCREEN IMPLEMENTATIONS ---

@Composable
fun PayCalculatorScreen(initialRate: String = "10", onBack: () -> Unit) {
    var hours by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf(initialRate) }
    var rangePerHour by remember { mutableStateOf("40") }
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
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("Pay Calculator", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            AuthInputField(
                label = "Seller Price (₹/hr)",
                value = rate,
                onValueChange = { rate = it },
                placeholder = "Enter host's price",
                trailingIcon = Icons.Default.CurrencyRupee,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AuthInputField(
                label = "Total Number of Hours",
                value = hours,
                onValueChange = { hours = it },
                placeholder = "Enter number of hours",
                trailingIcon = Icons.Default.AccessTime,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthInputField(
                label = "Vehicle Range per hour (km/hr)",
                value = rangePerHour,
                onValueChange = { rangePerHour = it },
                placeholder = "e.g. 40",
                trailingIcon = Icons.Default.ElectricScooter,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    val h = hours.toDoubleOrNull() ?: 0.0
                    val r = rate.toDoubleOrNull() ?: 0.0
                    val s = rangePerHour.toDoubleOrNull() ?: 0.0
                    totalAmount = h * r
                    totalDistance = h * s
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("CALCULATE", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            
            if (totalAmount != null) {
                Spacer(modifier = Modifier.height(48.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF152222)),
                    border = BorderStroke(1.dp, Color(0xFF00DC7F).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("total pay amount by traveller for hosts", color = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("₹${String.format("%.2f", totalAmount)}", style = MaterialTheme.typography.displayMedium, color = Color(0xFF00DC7F), fontWeight = FontWeight.Black)

                        totalDistance?.let { dist ->
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("total distance can travel after charge", color = Color.White.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${String.format("%.1f", dist)} km", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { /* Process Payment Logic */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("PAY NOW", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7).copy(alpha = 0.5f), Color.White)
                )
            )
    ) {
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = null,
            tint = Color(0xFF10B981).copy(alpha = 0.1f),
            modifier = Modifier.size(80.dp).align(Alignment.TopEnd).offset(x = 20.dp, y = 100.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF1F2937))) { append("Powering Communities,\n") }
                    withStyle(style = SpanStyle(color = Color(0xFF10B981))) { append("Empowering Futures") }
                },
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, lineHeight = 34.sp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Empowering rural India with sustainable\nmobility and micro-entrepreneurship.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.width(24.dp).height(3.dp).background(Color(0xFF10B981), RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.height(40.dp))
            Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(240.dp).background(Color(0xFF10B981).copy(alpha = 0.03f), CircleShape)
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(modifier = Modifier.size(180.dp), shape = CircleShape, color = Color.White, shadowElevation = 12.dp) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Bolt, null, modifier = Modifier.size(100.dp), tint = Color(0xFF10B981))
                        }
                    }
                }
                SatelliteIcon(Icons.Default.Eco, Modifier.align(Alignment.TopStart).offset(x = 30.dp, y = 30.dp))
                SatelliteIcon(Icons.Default.BatteryChargingFull, Modifier.align(Alignment.TopEnd).offset(x = (-30).dp, y = 30.dp))
                SatelliteIcon(Icons.Default.Groups, Modifier.align(Alignment.BottomStart).offset(x = 30.dp, y = (-30).dp))
                SatelliteIcon(Icons.Default.ElectricScooter, Modifier.align(Alignment.BottomEnd).offset(x = (-30).dp, y = (-30).dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("EV-Grama", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = Color(0xFF1F2937)))
            Text("COMMUNITY CHARGING", style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFF10B981), fontWeight = FontWeight.Bold, letterSpacing = 4.sp))
            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                repeat(3) { i -> Box(modifier = Modifier.padding(4.dp).size(if (i == 0) 10.dp else 8.dp).background(if (i == 0) Color(0xFF10B981) else Color.LightGray, CircleShape)) }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.ElectricScooter, "Bike", tint = Color(0xFF10B981), modifier = Modifier.size(70.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier.height(56.dp).weight(1f).padding(start = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("NEXT", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun LandingScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit, onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                LogoHeader()
                Spacer(modifier = Modifier.height(64.dp))
                EVIllustration()
            }
            Column(
                modifier = Modifier.align(Alignment.BottomCenter).background(Color(0xFF091010)).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    val brush = Brush.linearGradient(listOf(Color(0xFF00C853), Color(0xFF00E676)))
                    Box(modifier = Modifier.fillMaxSize().background(brush, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text("LOGIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                    Text(text = "OR", modifier = Modifier.padding(horizontal = 12.dp), color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF00DC7F)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00DC7F))
                ) {
                    Text("REGISTER", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("New to EV? ", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    TextButton(onClick = onRegisterClick, contentPadding = PaddingValues(0.dp)) {
                        Text("Create an account", color = Color(0xFF00DC7F), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }
    }
}

@Composable
fun AuthDetailsScreen(isLoginMode: Boolean, onSuccess: () -> Unit, onBack: () -> Unit, errorMessage: String? = null) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                LogoHeader()
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = if (isLoginMode) "Login" else "Register",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF00DC7F), fontSize = 32.sp),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Yes it's a Silent Start", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.6f)), modifier = Modifier.fillMaxWidth())
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorMessage, color = Color.Red, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
                }
                Spacer(modifier = Modifier.height(32.dp))
                if (!isLoginMode) {
                    AuthInputField(label = "Full Name", value = name, onValueChange = { name = it }, placeholder = "Enter your full name", trailingIcon = Icons.Default.Person)
                    Spacer(modifier = Modifier.height(20.dp))
                }
                AuthInputField(label = "Email", value = email, onValueChange = { email = it }, placeholder = "Enter your email", trailingIcon = Icons.Default.Email, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                Spacer(modifier = Modifier.height(20.dp))
                AuthInputField(label = "Password", value = password, onValueChange = { password = it }, placeholder = "Enter your password", trailingIcon = Icons.Default.Lock, isPassword = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter).background(Color(0xFF091010)).padding(24.dp)) {
                Button(
                    onClick = onSuccess,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F)),
                    contentPadding = PaddingValues()
                ) {
                    val brush = Brush.linearGradient(listOf(Color(0xFF00C853), Color(0xFF00E676)))
                    Box(modifier = Modifier.fillMaxSize().background(brush, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text(if (isLoginMode) "SIGN IN" else "SIGN UP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
            IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }
    }
}

@Composable
fun UserSelectionScreen(onRoleSelected: (isTraveller: Boolean) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Select Your Role", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00DC7F))
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onRoleSelected(true) }) {
                    Surface(shape = CircleShape, color = Color(0xFF1A2A2A), modifier = Modifier.size(100.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, contentDescription = "Traveller", modifier = Modifier.size(50.dp), tint = Color(0xFF00DC7F)) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Traveller", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onRoleSelected(false) }) {
                    Surface(shape = CircleShape, color = Color(0xFF1A2A2A), modifier = Modifier.size(100.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Store, contentDescription = "Hosts", modifier = Modifier.size(50.dp), tint = Color(0xFF00DC7F)) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hosts", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun TravellerDetailsScreen(onConfirm: () -> Unit, onBack: () -> Unit) {
    var vehicleName by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var adapterConfig by remember { mutableStateOf("5A") }
    var currentPercentage by remember { mutableFloatStateOf(50f) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                LogoHeader()
                Spacer(modifier = Modifier.height(48.dp))
                Text("Traveller Details", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00DC7F), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                AuthInputField(label = "Vehicle Name", value = vehicleName, onValueChange = { vehicleName = it }, placeholder = "e.g. Tesla Model 3", trailingIcon = Icons.Default.DirectionsCar)
                Spacer(modifier = Modifier.height(16.dp))
                AuthInputField(label = "Vehicle Number", value = vehicleNumber, onValueChange = { vehicleNumber = it }, placeholder = "e.g. ABC 1234", trailingIcon = Icons.Default.Numbers)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Adapter Configuration", color = Color.White, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("5A", "10A", "15A").forEach {
                        FilterChip(
                            selected = adapterConfig == it,
                            onClick = { adapterConfig = it },
                            label = { Text(it) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF00DC7F), selectedLabelColor = Color.Black, labelColor = Color.White)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Current Charging Percentage: ${currentPercentage.toInt()}%", color = Color.White, modifier = Modifier.fillMaxWidth())
                Slider(value = currentPercentage, onValueChange = { currentPercentage = it }, valueRange = 0f..100f, colors = SliderDefaults.colors(thumbColor = Color(0xFF00DC7F), activeTrackColor = Color(0xFF00DC7F)))
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F)),
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

@Composable
fun HostDetailsScreen(onConfirm: (ChargingShop) -> Unit, onBack: () -> Unit) {
    var shopName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var adapterType by remember { mutableStateOf("5A") }
    var pricePerHour by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                LogoHeader()
                Spacer(modifier = Modifier.height(48.dp))
                Text("Host Details", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00DC7F), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                AuthInputField(label = "Shop Name", value = shopName, onValueChange = { shopName = it }, placeholder = "e.g. Green Energy Hub", trailingIcon = Icons.Default.Store)
                Spacer(modifier = Modifier.height(16.dp))
                AuthInputField(label = "Contact Number", value = contactNumber, onValueChange = { contactNumber = it }, placeholder = "e.g. +91 999 000 111", trailingIcon = Icons.Default.Phone, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                Spacer(modifier = Modifier.height(16.dp))
                AuthInputField(label = "Location", value = location, onValueChange = { location = it }, placeholder = "Enter shop address", trailingIcon = Icons.Default.LocationOn)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Type of Adaptor", color = Color.White, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("5A", "10A", "15A").forEach {
                        FilterChip(
                            selected = adapterType == it,
                            onClick = { adapterType = it },
                            label = { Text(it) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF00DC7F), selectedLabelColor = Color.Black, labelColor = Color.White)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                AuthInputField(label = "Price Per Hour (₹)", value = pricePerHour, onValueChange = { pricePerHour = it }, placeholder = "e.g. 50", trailingIcon = Icons.Default.CurrencyRupee, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)) {
                Button(
                    onClick = {
                        val newShop = ChargingShop(shopName.ifBlank { "New Shop" }, "0.5 km", adapterType, contactNumber, "₹${pricePerHour.ifBlank { "0" }}", 5.0f, 47.6128, -122.3214)
                        onConfirm(newShop)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F)),
                    contentPadding = PaddingValues()
                ) {
                    val brush = Brush.linearGradient(listOf(Color(0xFF00C853), Color(0xFF00E676)))
                    Box(modifier = Modifier.fillMaxSize().background(brush, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text("START HOSTING", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }
    }
}

@Composable
fun EVGramaMapScreen(
    shops: List<ChargingShop>,
    travellerLocation: LatLng,
    onBack: () -> Unit,
    onChargeNow: (ChargingShop) -> Unit,
    onUpdateLocation: (LatLng) -> Unit,
    initialSelectedShop: ChargingShop? = null
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(travellerLocation, 15f)
    }
    var selectedShop by remember { mutableStateOf(initialSelectedShop) }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    // Custom state to track if user explicitly allowed live location within this session
    var isLocationPermissionGranted by rememberSaveable { mutableStateOf(false) }
    // State to show permission confirmation dialog
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Sync camera if live location updates
    LaunchedEffect(travellerLocation) {
        cameraPositionState.animate(update = CameraUpdateFactory.newLatLng(travellerLocation))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fullscreen Google Map
        if (LocalInspectionMode.current) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE5E7EB)), contentAlignment = Alignment.Center) {
                Text("Google Map Fullscreen Preview", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = true
                ),
                properties = MapProperties(
                    // Enabled only after user gives permission through the custom dialog
                    isMyLocationEnabled = isLocationPermissionGranted 
                )
            ) {
                // Traveller Marker (Blue icon to show current location)
                // Visible only after user gives permission
                if (isLocationPermissionGranted) {
                    Marker(
                        state = rememberMarkerState(position = travellerLocation),
                        title = "Your Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }

                shops.forEach { shop ->
                    Marker(
                        state = rememberMarkerState(position = LatLng(shop.lat, shop.lng)),
                        title = shop.name,
                        snippet = "${shop.adapterType} • ${shop.price} • ${if (shop.isAvailable) "Available" else "Busy"}",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (shop.isAvailable) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED
                        ),
                        onClick = {
                            selectedShop = shop
                            false
                        }
                    )
                }
            }
        }

        // Floating Search Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF10B981))
                }
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search EV-Grama Charge...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            }
        }

        // Live Location Button (Blue Circle) - Shows Dialog if permission not yet granted
        FloatingActionButton(
            onClick = { 
                if (!isLocationPermissionGranted) {
                    showPermissionDialog = true
                } else {
                    scope.launch {
                        // Update location in Firebase (Simulated live update)
                        onUpdateLocation(travellerLocation)
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(travellerLocation, 15f),
                            durationMs = 1000
                        )
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = if (selectedShop != null) 300.dp else 32.dp, end = 16.dp),
            containerColor = Color(0xFF2196F3),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Live Location")
        }

        // Bottom Details Card
        if (selectedShop != null) {
            val shop = selectedShop!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1818)),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            // Green Seller/Host Icon
                            Icon(
                                Icons.Default.Storefront, 
                                contentDescription = null, 
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(32.dp).padding(end = 8.dp)
                            )
                            Column {
                                Text(shop.name, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(shop.adapterType, color = Color(0xFF10B981), fontWeight = FontWeight.Medium)
                            }
                        }
                        Surface(
                            color = if (shop.isAvailable) Color(0xFF10B981).copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (shop.isAvailable) "AVAILABLE" else "BUSY",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = if (shop.isAvailable) Color(0xFF10B981) else Color.Red,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                        Text(" ${shop.price}/hr", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(18.dp))
                        Text(" ${shop.rating}", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onChargeNow(shop) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CHARGE NOW", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Live Location Permission Confirmation Dialog
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = { Text("Allow Live Location", color = Color.White) },
                text = { Text("To help you reach the host's shop easily, please allow access to your live location and connect to google app.", color = Color.White.copy(alpha = 0.8f)) },
                confirmButton = {
                    TextButton(onClick = {
                        showPermissionDialog = false
                        isLocationPermissionGranted = true // Mark as allowed
                        scope.launch {
                            // Update location in Firebase (Simulated live update)
                            onUpdateLocation(travellerLocation)
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(travellerLocation, 15f),
                                durationMs = 1000
                            )
                        }
                    }) {
                        Text("ALLOW", color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionDialog = false }) {
                        Text("CANCEL", color = Color.White.copy(alpha = 0.6f))
                    }
                },
                containerColor = Color(0xFF152222),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun DirectionsScreen(shop: ChargingShop, travellerLocation: LatLng, onBack: () -> Unit, onGoToPayCalculator: () -> Unit) {
    val shopLocation = LatLng(shop.lat, shop.lng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(travellerLocation, 14f)
    }

    // Sync camera if live location updates
    LaunchedEffect(travellerLocation) {
        cameraPositionState.animate(update = CameraUpdateFactory.newLatLng(travellerLocation))
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Text("Route to ${shop.name}", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
                }
                
                // Map section showing route
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                ) {
                    if (LocalInspectionMode.current) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                            Text("Map showing route to ${shop.name}", color = Color.White)
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
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                            )
                            Marker(
                                state = rememberMarkerState(position = shopLocation),
                                title = shop.name,
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            )
                            Polyline(
                                points = listOf(travellerLocation, shopLocation),
                                color = Color(0xFF795548), // Brown color path
                                width = 10f
                            )
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().weight(1.2f).padding(24.dp)) {
                    Text("Distance: ${shop.distance}", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Step-by-step directions in brown
                    val directions = listOf(
                        "Head North on Main St toward 5th Ave",
                        "Turn right onto 5th Ave",
                        "Continue for 200m",
                        "Turn left onto Grama Road",
                        "Your destination will be on the right"
                    )
                    
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
                        items(directions) { step ->
                            Row(verticalAlignment = Alignment.Top) {
                                Box(modifier = Modifier.padding(top = 6.dp).size(8.dp).background(Color(0xFF795548), CircleShape))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(step, color = Color(0xFF795548), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Instructional Text
                    Text(
                        text = "After charge is completed go for pay calculator",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pay Calculator Button
                    Button(
                        onClick = onGoToPayCalculator,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("PAY CALCULATOR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SatelliteIcon(icon: ImageVector, modifier: Modifier) {
    Surface(modifier = modifier.size(48.dp), shape = CircleShape, color = Color.White, shadowElevation = 6.dp) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp)) }
    }
}

@Composable
fun ShopSelectionScreen(shops: List<ChargingShop>, travellerLocation: LatLng, onShopSelected: (ChargingShop) -> Unit, onBack: () -> Unit) {
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(travellerLocation, 15f) }
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF091010)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (LocalInspectionMode.current) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE5E7EB)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Map, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                            Text("Live Map (Nearby Shops)", color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState, uiSettings = MapUiSettings(zoomControlsEnabled = false)) {
                        Marker(state = rememberMarkerState(position = travellerLocation), title = "Your Location", icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        shops.forEach { shop ->
                            val markerColor = when (shop.adapterType) {
                                "Mobile Shop" -> BitmapDescriptorFactory.HUE_AZURE
                                "Stationary Shop" -> BitmapDescriptorFactory.HUE_ORANGE
                                "Grocery Shop" -> BitmapDescriptorFactory.HUE_YELLOW
                                "Lubricants Shop" -> BitmapDescriptorFactory.HUE_VIOLET
                                "Bakery Shop" -> BitmapDescriptorFactory.HUE_ROSE
                                "Hotel" -> BitmapDescriptorFactory.HUE_BLUE
                                else -> BitmapDescriptorFactory.HUE_GREEN
                            }
                            Marker(state = rememberMarkerState(position = LatLng(shop.lat, shop.lng)), title = shop.name, snippet = shop.adapterType, icon = BitmapDescriptorFactory.defaultMarker(markerColor))
                        }
                    }
                }
                IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).align(Alignment.TopStart).background(Color.Black.copy(alpha = 0.4f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            }
            Column(modifier = Modifier.fillMaxWidth().weight(1.2f).background(Color(0xFF091010)).padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Nearby Shops & Services", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
                Text("Explore local amenities in your vicinity", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(shops) { shop -> ShopCard(shop, onShopSelected) }
                }
            }
        }
    }
}

@Composable
fun ShopCard(shop: ChargingShop, onClick: (ChargingShop) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick(shop) }, colors = CardDefaults.cardColors(containerColor = Color(0xFF152222)), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(shop.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                Text(shop.distance, color = Color(0xFF00DC7F), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                Text(" ${shop.rating} • ${shop.adapterType}", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Text(" ${shop.contact}", color = Color.Gray, fontSize = 12.sp)
            } 
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(shop.price, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Button(onClick = { onClick(shop) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00DC7F)), shape = RoundedCornerShape(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storefront, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("VIEW SHOP", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun LogoHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.TopEnd) {
            Text(text = "EV", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF00DC7F), fontSize = 72.sp, letterSpacing = (-2).sp))
            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF00DC7F), modifier = Modifier.size(36.dp).offset(x = 12.dp, y = 8.dp))
        }
        Text(text = "Yes it's a Silent Start", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.offset(y = (-8).dp))
    }
}

@Composable
fun AuthInputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, trailingIcon: ImageVector, isPassword: Boolean = false, keyboardOptions: KeyboardOptions = KeyboardOptions.Default) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp) },
            trailingIcon = { Icon(trailingIcon, null, tint = Color.White.copy(alpha = 0.4f)) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = keyboardOptions, shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFF0F1818), unfocusedContainerColor = Color(0xFF0F1818), focusedBorderColor = Color(0xFF00DC7F), unfocusedBorderColor = Color.White.copy(alpha = 0.1f), focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color(0xFF00DC7F)),
            singleLine = true
        )
    }
}

@Composable
fun EVIllustration() {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(300.dp).background(Brush.radialGradient(colors = listOf(Color(0xFF00DC7F).copy(alpha = 0.08f), Color.Transparent))))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
            Box(modifier = Modifier.size(width = 180.dp, height = 90.dp).background(Color(0xFF0A1212), RoundedCornerShape(topStart = 40.dp, topEnd = 20.dp, bottomStart = 8.dp, bottomEnd = 8.dp)).border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(topStart = 40.dp, topEnd = 20.dp, bottomStart = 8.dp, bottomEnd = 8.dp))) {
                Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp).size(35.dp, 10.dp).background(Color(0xFF00DC7F).copy(alpha = 0.6f), CircleShape))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(width = 70.dp, height = 150.dp).background(Color(0xFF121B1B), RoundedCornerShape(12.dp)).border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))) {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.fillMaxWidth().height(25.dp).background(Color(0xFF00DC7F).copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Bolt, null, tint = Color(0xFF00DC7F), modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

// --- CENTRALIZED PREVIEWS ---

@Preview(showBackground = true, showSystemUi = true, name = "00. Full App Flow")
@Composable
fun FullAppPreview() { MaterialTheme { Surface(Modifier.fillMaxSize()) { AppNavigation() } } }

@Preview(showBackground = true, name = "01. Welcome")
@Composable
fun PreviewWelcome() { MaterialTheme { WelcomeScreen { } } }

@Preview(showBackground = true, name = "04. Landing (Login/Register)")
@Composable
fun PreviewLanding() { MaterialTheme { LandingScreen(onLoginClick = {}, onRegisterClick = {}, onBack = {}) } }

@Preview(showBackground = true, name = "05. Login Details")
@Composable
fun PreviewLogin() { MaterialTheme { AuthDetailsScreen(isLoginMode = true, onSuccess = {}, onBack = {}) } }

@Preview(showBackground = true, name = "06. Register Details")
@Composable
fun PreviewRegister() { MaterialTheme { AuthDetailsScreen(isLoginMode = false, onSuccess = {}, onBack = {}) } }

@Preview(showBackground = true, name = "07. User Selection")
@Composable
fun PreviewUserSelection() { MaterialTheme { UserSelectionScreen(onRoleSelected = {}) } }

@Preview(showBackground = true, name = "08. Traveller Details")
@Composable
fun PreviewTravellerDetails() { MaterialTheme { TravellerDetailsScreen(onConfirm = {}, onBack = {}) } }

@Preview(showBackground = true, name = "09. Host Details")
@Composable
fun PreviewHostDetails() { MaterialTheme { HostDetailsScreen(onConfirm = {}, onBack = {}) } }

@Preview(showBackground = true, name = "12. Shop Selection & Map")
@Composable
fun PreviewShopSelection() { 
    val mockShops = listOf(ChargingShop("iTech Mobile Store", "0.5 km", "Mobile Shop", "+91 999 000 111", "Open", 4.5f, 47.6128, -122.3214))
    MaterialTheme { ShopSelectionScreen(mockShops, LatLng(47.6128, -122.3214), { _ -> }, {}) }
}

@Preview(showBackground = true, name = "13. EV-Grama Map Screen")
@Composable
fun PreviewEVGramaMap() {
    val mockShops = listOf(
        ChargingShop("Grama Charge A", "0.5 km", "15A", "+91 999 000 111", "₹50", 4.5f, 47.6128, -122.3214, isAvailable = true),
        ChargingShop("Grama Charge B", "0.8 km", "5A", "+91 999 000 222", "₹30", 4.2f, 47.6148, -122.3234, isAvailable = false)
    )
    MaterialTheme { 
        EVGramaMapScreen(
            shops = mockShops, 
            travellerLocation = LatLng(47.6128, -122.3214),
            onBack = {},
            onChargeNow = {},
            onUpdateLocation = {},
            initialSelectedShop = mockShops[0] // Show the seller details card in preview
        ) 
    }
}

@Preview(showBackground = true, name = "14. Directions Screen")
@Composable
fun PreviewDirections() {
    val mockShop = ChargingShop("Grama Charge A", "0.5 km", "15A", "+91 999 000 111", "₹50", 4.5f, 47.6128, -122.3214, isAvailable = true)
    MaterialTheme { DirectionsScreen(shop = mockShop, travellerLocation = LatLng(47.6128, -122.3214), onBack = {}, onGoToPayCalculator = {}) }
}

@Preview(showBackground = true, name = "15. Pay Calculator")
@Composable
fun PreviewPayCalculator() { MaterialTheme { PayCalculatorScreen(onBack = {}) } }

@Preview(showBackground = true, name = "Component: EV Illustration")
@Composable
fun PreviewEVIllustration() {
    MaterialTheme {
        Box(modifier = Modifier.background(Color(0xFF091010)).padding(16.dp)) {
            EVIllustration()
        }
    }
}

@Preview(showBackground = true, name = "Component: Shop Card")
@Composable
fun PreviewShopCard() {
    val mockShop = ChargingShop("iTech Mobile Store", "0.5 km", "Mobile Shop", "+91 999 000 111", "Open", 4.5f, 47.6128, -122.3214)
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ShopCard(shop = mockShop, onClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Login with Error")
@Composable
fun PreviewLoginError() {
    MaterialTheme {
        AuthDetailsScreen(
            isLoginMode = true,
            onSuccess = {},
            onBack = {},
            errorMessage = "Invalid email or password"
        )
    }
}
