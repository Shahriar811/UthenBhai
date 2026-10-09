package com.mdshahriar.uthenbhai.util

import com.google.android.gms.maps.model.LatLng
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.RouteInfo
import com.mdshahriar.uthenbhai.model.VehicleType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object RouteHelper {

    fun calculateHaversineDistanceKm(from: LatLng, to: LatLng): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLng = Math.toRadians(to.longitude - from.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
                sin(dLng / 2) * sin(dLng / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        // Multiply by 1.25 road routing detour factor for realistic street navigation
        val straightLine = earthRadiusKm * c
        val roadDetourFactor = 1.25
        return (straightLine * roadDetourFactor * 10).roundToInt() / 10.0
    }

    fun estimatedDurationMinutes(distanceKm: Double, vehicleType: VehicleType): Int {
        // Offline fallback only; road routing replaces this estimate when available.
        val estimatedSpeedKmh = when (vehicleType) {
            VehicleType.BIKE -> 24.0
            VehicleType.CNG -> 19.0
            VehicleType.CAR -> 21.0
        }
        return max(4, ((distanceKm / estimatedSpeedKmh) * 60 + 3).roundToInt())
    }

    fun generateRoute(pickup: AppLocation, destination: AppLocation,
                      vehicleType: VehicleType = VehicleType.BIKE): RouteInfo {
        val distanceKm = max(0.5, calculateHaversineDistanceKm(pickup.latLng, destination.latLng))
        val durationMins = estimatedDurationMinutes(distanceKm, vehicleType)

        // Generate street waypoints between points
        val waypoints = mutableListOf<LatLng>()
        waypoints.add(pickup.latLng)

        val steps = max(6, (distanceKm * 2.5).roundToInt())
        for (i in 1 until steps) {
            val fraction = i.toDouble() / steps
            val lat = pickup.latitude + (destination.latitude - pickup.latitude) * fraction
            val lng = pickup.longitude + (destination.longitude - pickup.longitude) * fraction

            // Add subtle road bends to simulate street grid
            val bendFactor = sin(fraction * Math.PI) * 0.0022 * (if (i % 2 == 0) 1 else -1)
            waypoints.add(LatLng(lat + bendFactor, lng + (bendFactor * 0.6)))
        }
        waypoints.add(destination.latLng)

        return RouteInfo(
            pickup = pickup,
            destination = destination,
            distanceKm = distanceKm,
            durationMinutes = durationMins,
            pathPoints = waypoints
        )
    }
}
