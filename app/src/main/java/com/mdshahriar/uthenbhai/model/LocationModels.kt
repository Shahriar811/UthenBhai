package com.mdshahriar.uthenbhai.model

import com.google.android.gms.maps.model.LatLng

data class AppLocation(
    val id: String = "",
    val name: String,
    val address: String,
    val latLng: LatLng
) {
    val latitude: Double get() = latLng.latitude
    val longitude: Double get() = latLng.longitude
}

data class RouteInfo(
    val pickup: AppLocation,
    val destination: AppLocation,
    val distanceKm: Double,
    val durationMinutes: Int,
    val pathPoints: List<LatLng>
)

data class FareBreakdown(
    val distanceKm: Double,
    val durationMinutes: Int,
    val vehicleType: VehicleType = VehicleType.BIKE,
    val baseFare: Int,
    val distanceCost: Int,
    val timeCost: Int,
    val minimumFareAdjustmentTk: Int,
    val busyHourAdjustmentTk: Int,
    val busyHourPercent: Int,
    val discountTk: Int = 0,
    val totalFareTk: Int,
    val paymentMethod: String = "Cash to driver"
)
