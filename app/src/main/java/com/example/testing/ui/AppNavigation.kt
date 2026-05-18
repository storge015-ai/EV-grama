package com.example.testing.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.example.testing.data.AppScreen
import com.example.testing.data.ChargingShop
import com.example.testing.data.FirebaseRepository
import com.example.testing.ui.screens.*
import com.google.android.gms.maps.model.LatLng

@Composable
fun AppNavigation() {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Welcome) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var selectedShopForDirections by remember { mutableStateOf<ChargingShop?>(null) }
    var isUserTraveller by rememberSaveable { mutableStateOf(true) }

    val repository = remember { FirebaseRepository() }
    val isPreview = LocalInspectionMode.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (!isPreview) {
            launcher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
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
        if (!isPreview) {
            repository.observeShops { updatedShops ->
                if (updatedShops.isNotEmpty()) {
                    shops.clear()
                    shops.addAll(updatedShops)
                }
            }
            repository.observeTravellerLocation { location -> liveTravellerLocation = location }
            repository.observeHostLocation { location -> liveHostLocation = location }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
                    if (!isPreview) repository.addShop(newShop)
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

            AppScreen.TravellerDetails -> TravellerDetailsScreen(
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
}
