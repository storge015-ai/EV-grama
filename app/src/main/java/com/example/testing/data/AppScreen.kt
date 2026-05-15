package com.example.testing.data

/**
 * Enum representing the different screens in the application.
 * Moved to its own file to resolve ClassNotFoundException in Compose Preview.
 */
enum class AppScreen {
    Welcome, 
    Onboarding1, 
    Onboarding2, 
    Onboarding3, 
    Landing, 
    LoginDetails, 
    RegisterDetails, 
    UserSelection, 
    TravellerDetails, 
    SellerDetails, 
    SellerSuccess, 
    ShopSelection, 
    Charging, 
    EVGramaMap,
    Directions,
    PayCalculator
}
