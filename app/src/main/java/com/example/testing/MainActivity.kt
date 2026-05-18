package com.example.testing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testing.data.ChargingShop
import com.example.testing.ui.AppNavigation
import com.example.testing.ui.screens.*
import com.example.testing.ui.theme.TestingTheme
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.database

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

// --- APP FLOW PREVIEW ---

@Preview(showBackground = true, showSystemUi = true, name = "Full App Navigation")
@Composable
fun AppNavigationPreview() {
    TestingTheme {
        AppNavigation()
    }
}

// --- INDIVIDUAL SCREEN PREVIEWS ---

@Preview(showBackground = true, name = "1. Welcome Screen")
@Composable
fun WelcomePreview() {
    TestingTheme { WelcomeScreen(onNext = {}) }
}

@Preview(showBackground = true, name = "2. Onboarding 1 (Map)")
@Composable
fun Onboarding1Preview() {
    TestingTheme {
        OnboardingScreen(
            title = "Find and located charging Stations Near you",
            subtitle = "Find available charging points easily and reserve your charging point",
            image = Icons.Default.Map,
            pageIndex = 0,
            onNext = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Onboarding 2 (Payment)")
@Composable
fun Onboarding2Preview() {
    TestingTheme {
        OnboardingScreen(
            title = "Book and pay at any time from anywhere",
            subtitle = "Reserve your slot as per your choice and then pay online to confirm booking",
            image = Icons.Default.Payment,
            pageIndex = 1,
            onNext = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Onboarding 3 (Relax)")
@Composable
fun Onboarding3Preview() {
    TestingTheme {
        OnboardingScreen(
            title = "Sit back and relax, let your EV charge",
            subtitle = "Reach the charging point per your slot and charge comfortably",
            image = Icons.Default.ElectricScooter,
            pageIndex = 2,
            onNext = {},
            onSkip = {},
            isLastPage = true
        )
    }
}

@Preview(showBackground = true, name = "3. Landing Screen")
@Composable
fun LandingPreview() {
    TestingTheme { LandingScreen(onLoginClick = {}, onRegisterClick = {}, onBack = {}) }
}

@Preview(showBackground = true, name = "4. Login Screen")
@Composable
fun LoginPreview() {
    TestingTheme { AuthDetailsScreen(isLoginMode = true, onSuccess = {}, onBack = {}) }
}

@Preview(showBackground = true, name = "4. Register Screen")
@Composable
fun RegisterPreview() {
    TestingTheme { AuthDetailsScreen(isLoginMode = false, onSuccess = {}, onBack = {}) }
}

@Preview(showBackground = true, name = "5. User Selection")
@Composable
fun UserSelectionPreview() {
    TestingTheme { UserSelectionScreen(onRoleSelected = {}) }
}

@Preview(showBackground = true, name = "6. Host Details")
@Composable
fun HostDetailsPreview() {
    TestingTheme { HostDetailsScreen(onConfirm = {}, onBack = {}) }
}

@Preview(showBackground = true, name = "7. Host Success")
@Composable
fun HostSuccessPreview() {
    TestingTheme { HostSuccessScreen(onDone = {}, onBack = {}) }
}

@Preview(showBackground = true, name = "8. Traveller Details")
@Composable
fun TravellerDetailsPreview() {
    TestingTheme { TravellerDetailsScreen(onConfirm = {}, onBack = {}) }
}

@Preview(showBackground = true, name = "11. Shop Selection")
@Composable
fun ShopSelectionPreview() {
    TestingTheme {
        ShopSelectionScreen(
            shops = listOf(
                ChargingShop("Vikas Stationary", "0.8 km", "5A", "+91 111", "₹30", 4.2f, 0.0, 0.0, false)
            ),
            onShopSelected = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "12. Route Directions")
@Composable
fun DirectionsPreview() {
    TestingTheme {
        RouteDirectionsScreen(
            shop = ChargingShop("iTech Store", "0.5 km", "15A", "+91 000", "₹50", 4.8f, 0.0, 0.0),
            travellerLocation = LatLng(0.0, 0.0),
            onBack = {},
            onArrivedAtShop = {}
        )
    }
}

@Preview(showBackground = true, name = "13. Charging Screen")
@Composable
fun ChargingPreview() {
    TestingTheme { ChargingScreen(onLogout = {}, onGoToPayCalculator = {}) }
}

@Preview(showBackground = true, name = "14. Pay Calculator")
@Composable
fun PayCalculatorPreview() {
    TestingTheme { PayCalculatorScreen(onBack = {}) }
}
