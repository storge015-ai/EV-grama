package com.example.testing.data

data class ChargingShop(
    val name: String = "",
    val distance: String = "",
    val adapterType: String = "",
    val contact: String = "",
    val price: String = "",
    val rating: Float = 0f,
    val lat: Double = 0.0, 
    val lng: Double = 0.0,
    val isAvailable: Boolean = true
)
