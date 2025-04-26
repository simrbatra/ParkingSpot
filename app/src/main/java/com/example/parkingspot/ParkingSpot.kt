package com.example.parkingspot.model


data class ParkingSpot(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isBooked: Boolean = false
)
