package com.mdshahriar.uthenbhai.model

import com.google.android.gms.maps.model.LatLng

enum class VehicleType(
    val title: String,
    val subtitle: String,
    val capacity: Int,
    val etaMinutes: Int,
    val badge: String,
    val baseFare: Int,
    val perKmRate: Double,
    val perMinuteRate: Double,
    val minFare: Int
) {
    BIKE(
        title = "Uthen Bike",
        subtitle = "Fastest through traffic",
        capacity = 1,
        etaMinutes = 2,
        badge = "FASTEST",
        baseFare = 30,
        perKmRate = 18.0,
        perMinuteRate = 1.2,
        minFare = 60
    ),
    CNG(
        title = "Uthen CNG Auto",
        subtitle = "Covered 3-wheeler for up to 3",
        capacity = 3,
        etaMinutes = 4,
        badge = "ECONOMY",
        baseFare = 55,
        perKmRate = 24.0,
        perMinuteRate = 1.8,
        minFare = 95
    ),
    CAR(
        title = "Uthen Car AC",
        subtitle = "Comfortable sedan with AC",
        capacity = 4,
        etaMinutes = 6,
        badge = "COMFORT AC",
        baseFare = 100,
        perKmRate = 38.0,
        perMinuteRate = 2.8,
        minFare = 180
    )
}

data class Driver(
    val id: String = "drv_001",
    val name: String = "Rahim Uddin",
    val phone: String = "+880 1712-345678",
    val rating: Double = 4.9,
    val completedRides: Int = 1420,
    val vehicleType: VehicleType = VehicleType.BIKE,
    val vehicleModel: String = "Yamaha FZ-S V3 (Matt Black)",
    val bikeModel: String = "Yamaha FZ-S V3 (Matt Black)", // Kept for backwards compatibility
    val plateNumber: String = "DHAKA METRO-HA 45-8912",
    val currentLocation: LatLng? = null
) {
    companion object {
        fun forVehicle(type: VehicleType): Driver {
            return when (type) {
                VehicleType.BIKE -> Driver(
                    id = "drv_bike_01",
                    name = "Rahim Uddin",
                    phone = "+880 1712-345678",
                    rating = 4.9,
                    completedRides = 1420,
                    vehicleType = VehicleType.BIKE,
                    vehicleModel = "Yamaha FZ-S V3 (Matt Black)",
                    bikeModel = "Yamaha FZ-S V3 (Matt Black)",
                    plateNumber = "DHAKA METRO-HA 45-8912"
                )
                VehicleType.CNG -> Driver(
                    id = "drv_cng_01",
                    name = "Abdul Motaleb",
                    phone = "+880 1819-876543",
                    rating = 4.85,
                    completedRides = 2190,
                    vehicleType = VehicleType.CNG,
                    vehicleModel = "Bajaj 4-Stroke CNG (Green)",
                    bikeModel = "Bajaj 4-Stroke CNG (Green)",
                    plateNumber = "DHAKA METRO-TH 14-3321"
                )
                VehicleType.CAR -> Driver(
                    id = "drv_car_01",
                    name = "Tanvir Hasan",
                    phone = "+880 1911-554433",
                    rating = 4.95,
                    completedRides = 980,
                    vehicleType = VehicleType.CAR,
                    vehicleModel = "Toyota Allion AC (Silver)",
                    bikeModel = "Toyota Allion AC (Silver)",
                    plateNumber = "DHAKA METRO-GA 33-7766"
                )
            }
        }
    }
}

enum class RideState {
    PERMISSION_REQUIRED,
    IDLE,
    PICKUP_DROP_SELECTION,
    ROUTE_PREVIEW,
    SEARCHING_DRIVER,
    MATCH_UNAVAILABLE,
    DRIVER_ON_WAY,
    DRIVER_AT_PICKUP,
    TRIP_IN_PROGRESS,
    PAYMENT_DUE,
    PROFILE_VIEW
}

data class UserProfile(
    val uid: String = "guest_user",
    val name: String = "Guest Rider",
    val email: String = "",
    val phone: String = "",
    val totalRides: Int = 0,
    val rating: Double = 0.0,
    val savedHome: String = "",
    val savedWork: String = ""
)

data class ActiveRide(
    val id: String = "ride_${System.currentTimeMillis()}",
    val pickup: AppLocation,
    val destination: AppLocation,
    val vehicleType: VehicleType = VehicleType.BIKE,
    val fareBreakdown: FareBreakdown,
    val driver: Driver = Driver(),
    val state: RideState = RideState.ROUTE_PREVIEW,
    val paymentMode: String = "Cash Direct",
    val promoCode: String? = null,
    val discountTk: Int = 0,
    val passengerNote: String = ""
)
