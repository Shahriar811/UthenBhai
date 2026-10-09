package com.mdshahriar.uthenbhai.ui.screens

import com.mdshahriar.uthenbhai.ui.theme.success

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import java.util.Locale
import com.mdshahriar.uthenbhai.data.GeoapifyService
import com.mdshahriar.uthenbhai.data.DeviceLocationService
import com.mdshahriar.uthenbhai.ui.components.LocationStatusScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import com.mdshahriar.uthenbhai.model.VehicleType
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInVertically
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.mdshahriar.uthenbhai.data.FirebaseRepository
import com.mdshahriar.uthenbhai.data.RideHistoryStore
import com.mdshahriar.uthenbhai.data.SavedPlacesStore
import com.mdshahriar.uthenbhai.data.RecentPlacesStore
import com.mdshahriar.uthenbhai.data.RideSession
import com.mdshahriar.uthenbhai.data.RideSessionStore
import androidx.compose.runtime.mutableStateListOf
import com.mdshahriar.uthenbhai.model.ActiveRide
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.Driver
import com.mdshahriar.uthenbhai.model.FareBreakdown
import com.mdshahriar.uthenbhai.model.RideState
import com.mdshahriar.uthenbhai.model.RouteInfo
import com.mdshahriar.uthenbhai.model.UserProfile
import com.mdshahriar.uthenbhai.ui.components.ActiveTripSheet
import com.mdshahriar.uthenbhai.ui.components.BrandLogo
import com.mdshahriar.uthenbhai.ui.components.CashPaymentReceiptDialog
import com.mdshahriar.uthenbhai.ui.components.CustomLocationPickerSheet
import com.mdshahriar.uthenbhai.ui.components.DriverMatchingOverlay
import com.mdshahriar.uthenbhai.ui.components.MatchUnavailableSheet
import com.mdshahriar.uthenbhai.ui.components.PickupVerificationSheet
import com.mdshahriar.uthenbhai.ui.components.CancelRideDialog
import com.mdshahriar.uthenbhai.ui.components.InteractiveStreetMap
import com.mdshahriar.uthenbhai.ui.components.LocationPermissionCard
import com.mdshahriar.uthenbhai.ui.components.RideBookingSheet
import com.mdshahriar.uthenbhai.ui.components.VehicleIcon
import com.mdshahriar.uthenbhai.ui.theme.OrangeAccent
import com.mdshahriar.uthenbhai.ui.locale.AppLanguage
import com.mdshahriar.uthenbhai.ui.locale.tr
import com.mdshahriar.uthenbhai.util.FareCalculator
import com.mdshahriar.uthenbhai.util.RouteHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MapEngine {
    GEOAPIFY_MAP, // Geoapify API live raster/vector tiles + live turn-by-turn routing
    GOOGLE_MAP    // Google Maps SDK
}

private enum class RouteQuality { NONE, CHECKING, ESTIMATED, ROAD }

@Composable
private fun stableMarkerState(position: LatLng): MarkerState {
    val state = remember { MarkerState(position = position) }
    SideEffect { state.position = position }
    return state
}

@Composable
fun MainRideScreen(
    currentUser: UserProfile,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val screenHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    val firebaseRepo = remember { FirebaseRepository(context) }

    val currentMapEngine = if (com.mdshahriar.uthenbhai.BuildConfig.MAPS_API_KEY.isBlank()) {
        MapEngine.GEOAPIFY_MAP
    } else {
        MapEngine.GOOGLE_MAP
    }
    var selectedTab by remember { mutableStateOf(RiderTab.RIDE) }
    val rideHistory = remember(currentUser.uid) {
        mutableStateListOf<com.mdshahriar.uthenbhai.data.RideHistoryEntry>().apply {
            addAll(RideHistoryStore.load(context, currentUser.uid))
        }
    }
    val restoredSession = remember(currentUser.uid) { RideSessionStore.load(context, currentUser.uid) }
    val restoredRoute = remember(currentUser.uid) {
        val p = restoredSession?.pickup
        val d = restoredSession?.destination
        if (p != null && d != null) {
            val fallback = RouteHelper.generateRoute(p, d,
                restoredSession?.vehicle ?: VehicleType.BIKE)
            fallback.copy(
                distanceKm = restoredSession?.routeDistanceKm ?: fallback.distanceKm,
                durationMinutes = restoredSession?.routeDurationMinutes ?: fallback.durationMinutes,
                pathPoints = restoredSession?.routePoints?.takeIf { it.isNotEmpty() }
                    ?: fallback.pathPoints
            )
        } else null
    }
    val restoredFare = remember(currentUser.uid) {
        restoredRoute?.let {
            FareCalculator.calculateFareWithPromo(it.distanceKm, it.durationMinutes,
                restoredSession?.vehicle ?: VehicleType.BIKE,
                restoredSession?.promoCode,
                restoredSession?.quoteEpochMillis ?: System.currentTimeMillis())
        }
    }

    var pickupLocation by remember { mutableStateOf(restoredSession?.pickup) }
    var destinationLocation by remember { mutableStateOf(restoredSession?.destination) }

    // Route & Fare (computed only when both locations are chosen)
    var routeInfo by remember { mutableStateOf(restoredRoute) }
    var fareBreakdown by remember { mutableStateOf(restoredFare) }
    var quoteEpochMillis by remember(currentUser.uid) {
        mutableStateOf(restoredSession?.quoteEpochMillis ?: System.currentTimeMillis())
    }

    // Active Ride State
    var rideState by remember { mutableStateOf(restoredSession?.state ?: RideState.IDLE) }
    var showRidePlanner by remember(currentUser.uid) {
        mutableStateOf(restoredSession?.state != null && restoredSession.state != RideState.IDLE)
    }

    var isSelectingPickup by remember { mutableStateOf(true) }
    var showLocationSearchSheet by remember { mutableStateOf(false) }
    val screenOpacity = remember { Animatable(1f) }
    LaunchedEffect(selectedTab, showRidePlanner) {
        screenOpacity.snapTo(0f)
        screenOpacity.animateTo(1f, tween(180))
    }
    val screenModifier = modifier.graphicsLayer { alpha = screenOpacity.value }

    // Vehicle, Payment & Promo States
    var selectedVehicleType by remember { mutableStateOf(restoredSession?.vehicle ?: VehicleType.BIKE) }
    var selectedPaymentMode by remember { mutableStateOf("Cash Direct") }
    var appliedPromoCode by remember { mutableStateOf(restoredSession?.promoCode) }
    var appliedDiscountTk by remember { mutableIntStateOf(restoredFare?.discountTk ?: 0) }
    var passengerNote by remember { mutableStateOf(restoredSession?.note.orEmpty()) }

    // Driver & Simulation
    var activeDriver by remember { mutableStateOf(Driver.forVehicle(selectedVehicleType)) }
    var simulatedDriverLocation by remember {
        mutableStateOf<LatLng?>(restoredSession?.driverPosition ?: if (rideState == RideState.DRIVER_ON_WAY && pickupLocation != null) {
            LatLng(pickupLocation!!.latitude + 0.005, pickupLocation!!.longitude + 0.005)
        } else if (rideState == RideState.DRIVER_AT_PICKUP) pickupLocation?.latLng else null)
    }
    var nextRoutePoint by remember { mutableIntStateOf(restoredSession?.nextRoutePoint ?: 0) }

    fun navigateToTab(tab: RiderTab) {
        selectedTab = tab
        showLocationSearchSheet = false
        showRidePlanner = false
        if (rideState in listOf(RideState.IDLE, RideState.ROUTE_PREVIEW)) rideState = RideState.IDLE
    }

    BackHandler(enabled = selectedTab != RiderTab.RIDE && rideState == RideState.IDLE) {
        navigateToTab(RiderTab.RIDE)
    }

    var currentRide by remember {
        mutableStateOf(restoredSession?.rideId?.let { id ->
            val p = restoredSession?.pickup
            val d = restoredSession?.destination
            val fare = restoredFare
            if (p != null && d != null && fare != null) ActiveRide(
                id = id, pickup = p, destination = d, vehicleType = selectedVehicleType,
                fareBreakdown = fare, driver = activeDriver, state = rideState,
                promoCode = appliedPromoCode, discountTk = appliedDiscountTk,
                passengerNote = passengerNote
            ) else null
        })
    }
    var routeQuality by remember {
        mutableStateOf(if (restoredRoute == null) RouteQuality.NONE else RouteQuality.ESTIMATED)
    }
    var routeRequestToken by remember { mutableIntStateOf(0) }
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser.uid, pickupLocation, destinationLocation, selectedVehicleType,
        rideState, currentRide?.id, routeInfo?.distanceKm, routeInfo?.durationMinutes,
        quoteEpochMillis, appliedPromoCode, appliedDiscountTk, passengerNote,
        routeInfo?.pathPoints, simulatedDriverLocation, nextRoutePoint) {
        if (pickupLocation == null && destinationLocation == null && rideState == RideState.IDLE) {
            RideSessionStore.clear(context, currentUser.uid)
        } else {
            val active = rideState in listOf(
                RideState.SEARCHING_DRIVER, RideState.MATCH_UNAVAILABLE,
                RideState.DRIVER_ON_WAY, RideState.DRIVER_AT_PICKUP,
                RideState.TRIP_IN_PROGRESS, RideState.PAYMENT_DUE
            )
            RideSessionStore.save(context, currentUser.uid, RideSession(
                pickup = pickupLocation, destination = destinationLocation,
                vehicle = selectedVehicleType, state = rideState,
                rideId = if (active) currentRide?.id else null,
                routeDistanceKm = routeInfo?.distanceKm,
                routeDurationMinutes = routeInfo?.durationMinutes,
                quoteEpochMillis = quoteEpochMillis,
                promoCode = appliedPromoCode, discountTk = appliedDiscountTk,
                note = passengerNote, routePoints = routeInfo?.pathPoints.orEmpty(),
                driverPosition = simulatedDriverLocation, nextRoutePoint = nextRoutePoint
            ))
        }
    }

    // The demo service boundary is independent of the user's map position.
    val serviceAreaCenter = LatLng(23.7808, 90.4167)
    val outsideDemoArea = pickupLocation?.let {
        RouteHelper.calculateHaversineDistanceKm(it.latLng, serviceAreaCenter) > 35.0
    } ?: false
    val cameraPositionState = rememberCameraPositionState()
    var cameraInitialized by remember { mutableStateOf(false) }
    var recenterRequest by remember { mutableIntStateOf(0) }

    var userCurrentLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocationAddress by remember { mutableStateOf<String?>(null) }
    var locating by remember { mutableStateOf(false) }
    var locationRequestJob by remember { mutableStateOf<Job?>(null) }

    fun refreshCurrentLocation(autoCenter: Boolean = false) {
        if (!hasLocationPermission) return
        locationRequestJob?.cancel()
        locationRequestJob = coroutineScope.launch {
            locating = true
            try {
                val point = DeviceLocationService.currentPosition(context)
                userCurrentLocation = point
                userLocationAddress = null
                if (point != null) {
                    if (autoCenter) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(point, 15f)
                        cameraInitialized = true
                        recenterRequest += 1
                    }
                    userLocationAddress = DeviceLocationService.address(context, point)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } finally {
                locating = false
            }
        }
    }

    fun requestRoadRoute(pickup: AppLocation, dropoff: AppLocation) {
        routeRequestToken += 1
        val requestToken = routeRequestToken
        if (GeoapifyService.apiKey.isBlank()) {
            routeQuality = RouteQuality.ESTIMATED
            return
        }
        routeQuality = RouteQuality.CHECKING
        coroutineScope.launch {
            val mode = if (selectedVehicleType == VehicleType.BIKE) "motorcycle" else "drive"
            val geoRoute = GeoapifyService.fetchRoute(pickup, dropoff, mode)
            if (routeRequestToken == requestToken &&
                pickupLocation?.latLng == pickup.latLng &&
                destinationLocation?.latLng == dropoff.latLng &&
                rideState in listOf(RideState.IDLE, RideState.ROUTE_PREVIEW)) {
                if (geoRoute == null) {
                    routeQuality = RouteQuality.ESTIMATED
                } else {
                    routeInfo = geoRoute
                    val fare = FareCalculator.calculateFareWithPromo(
                        geoRoute.distanceKm, geoRoute.durationMinutes, selectedVehicleType,
                        appliedPromoCode, quoteEpochMillis)
                    fareBreakdown = fare
                    appliedDiscountTk = fare.discountTk
                    currentRide = currentRide?.copy(fareBreakdown = fare)
                    routeQuality = RouteQuality.ROAD
                }
            }
        }
    }

    LaunchedEffect(currentUser.uid) {
        if (restoredSession?.state in listOf(RideState.IDLE, RideState.ROUTE_PREVIEW)) {
            val p = restoredSession?.pickup
            val d = restoredSession?.destination
            if (p != null && d != null) requestRoadRoute(p, d)
        }
    }

    // Function to calculate and update route
    fun calculateAndSetRoute(pickup: AppLocation?, dropoff: AppLocation?) {
        routeRequestToken += 1
        pickupLocation = pickup
        destinationLocation = dropoff
        isSelectingPickup = pickup == null

        if (pickup != null && dropoff != null) {
            if (rideState == RideState.IDLE) rideState = RideState.ROUTE_PREVIEW
            quoteEpochMillis = System.currentTimeMillis()
            val initialRoute = RouteHelper.generateRoute(pickup, dropoff, selectedVehicleType)
            val initialFare = FareCalculator.calculateFareWithPromo(initialRoute.distanceKm,
                initialRoute.durationMinutes, selectedVehicleType, appliedPromoCode,
                quoteEpochMillis)
            routeInfo = initialRoute
            fareBreakdown = initialFare
            appliedDiscountTk = initialFare.discountTk
            activeDriver = Driver.forVehicle(selectedVehicleType)
            currentRide = ActiveRide(
                pickup = pickup,
                destination = dropoff,
                vehicleType = selectedVehicleType,
                fareBreakdown = initialFare,
                driver = activeDriver,
                paymentMode = selectedPaymentMode,
                promoCode = appliedPromoCode,
                discountTk = appliedDiscountTk,
                passengerNote = passengerNote
            )

            requestRoadRoute(pickup, dropoff)

            // Zoom camera to cover both
            coroutineScope.launch {
                try {
                    val bounds = LatLngBounds.builder()
                        .include(pickup.latLng)
                        .include(dropoff.latLng)
                        .build()
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 180), 800)
                } catch (e: Exception) {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pickup.latLng, 14f), 800)
                }
            }
        } else {
            routeInfo = null
            fareBreakdown = null
            currentRide = null
            routeQuality = RouteQuality.NONE
            val targetPos = pickup?.latLng ?: dropoff?.latLng ?: userCurrentLocation
            if (targetPos != null) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(targetPos, 15f)
                cameraInitialized = true
            }
        }
    }

    // Handle map click
    fun onLocationClickedOnMap(latLng: LatLng) {
        if (showLocationSearchSheet || rideState !in listOf(RideState.IDLE, RideState.ROUTE_PREVIEW,
                RideState.PICKUP_DROP_SELECTION)) return
        if (isSelectingPickup) {
            val newPickup = AppLocation(
                id = "pickup_${System.currentTimeMillis()}",
                name = "Selected Pickup Pin",
                address = String.format(Locale.US, "%.4f, %.4f", latLng.latitude, latLng.longitude),
                latLng = latLng
            )
            calculateAndSetRoute(newPickup, destinationLocation)
            // If destination is still empty, prompt user to select destination
            if (destinationLocation == null) {
                isSelectingPickup = false
            }
        } else {
            val newDropoff = AppLocation(
                id = "dropoff_${System.currentTimeMillis()}",
                name = "Selected Destination Pin",
                address = String.format(Locale.US, "%.4f, %.4f", latLng.latitude, latLng.longitude),
                latLng = latLng
            )
            calculateAndSetRoute(pickupLocation, newDropoff)
        }

        if (pickupLocation != null && destinationLocation != null && rideState == RideState.IDLE) {
            rideState = RideState.ROUTE_PREVIEW
        }
    }

    DisposableEffect(lifecycleOwner, hasLocationPermission) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && hasLocationPermission) {
                refreshCurrentLocation(autoCenter = pickupLocation == null && destinationLocation == null)
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                locationRequestJob?.cancel()
                userCurrentLocation = null
                userLocationAddress = null
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            locationRequestJob?.cancel()
        }
    }

    val mapCenter = pickupLocation?.latLng ?: destinationLocation?.latLng ?: userCurrentLocation
    LaunchedEffect(mapCenter) {
        if (!cameraInitialized && mapCenter != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(mapCenter, 15f)
            cameraInitialized = true
        }
    }

    // Live Ride Simulation Step Machine
    LaunchedEffect(rideState) {
        when (rideState) {
            RideState.SEARCHING_DRIVER -> {
                nextRoutePoint = 0
                delay(2500)
                val connectivity = context.getSystemService(ConnectivityManager::class.java)
                val capabilities = connectivity?.getNetworkCapabilities(connectivity.activeNetwork)
                if (outsideDemoArea ||
                    capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) != true) {
                    rideState = RideState.MATCH_UNAVAILABLE
                } else {
                    val p = pickupLocation
                    if (p != null) {
                        simulatedDriverLocation = LatLng(p.latitude + 0.005, p.longitude + 0.005)
                    }
                    rideState = RideState.DRIVER_ON_WAY
                }
            }

            RideState.DRIVER_ON_WAY -> {
                val start = simulatedDriverLocation
                val end = pickupLocation?.latLng
                if (start != null && end != null) {
                    val steps = 12
                    for (i in 1..steps) {
                        delay(350)
                        val frac = i.toFloat() / steps
                        simulatedDriverLocation = LatLng(
                            start.latitude + (end.latitude - start.latitude) * frac,
                            start.longitude + (end.longitude - start.longitude) * frac
                        )
                    }
                }
                delay(800)
                rideState = RideState.DRIVER_AT_PICKUP
            }

            RideState.TRIP_IN_PROGRESS -> {
                val path = routeInfo?.pathPoints ?: emptyList()
                for (index in nextRoutePoint.coerceIn(0, path.size) until path.size) {
                    delay(350)
                    simulatedDriverLocation = path[index]
                    nextRoutePoint = index + 1
                }
                delay(600)
                rideState = RideState.PAYMENT_DUE
            }

            else -> Unit
        }
    }

    if (selectedTab != RiderTab.RIDE && rideState == RideState.IDLE) {
        RiderHubScreen(
            modifier = screenModifier,
            currentLocation = userCurrentLocation,
            tab = selectedTab,
            user = currentUser,
            history = rideHistory,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            appLanguage = appLanguage,
            onLanguageChange = onLanguageChange,
            onTabSelected = { navigateToTab(it) },
            onSignOut = {
                firebaseRepo.signOut()
                onSignOut()
            }
        )
        return
    }

    // Profile Screen Modal
    if (rideState == RideState.PROFILE_VIEW) {
        ProfileScreen(
            userProfile = currentUser,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            onBack = { rideState = RideState.IDLE },
            onSignOut = {
                firebaseRepo.signOut()
                onSignOut()
            }
        )
        return
    }

    // Cash Payment Receipt Modal
    if (rideState == RideState.PAYMENT_DUE && currentRide != null) {
        CashPaymentReceiptDialog(
            ride = currentRide!!,
            onPaymentConfirmed = { rating, tipTk ->
                val finishedRide = currentRide!!
                rideHistory.add(0, RideHistoryStore.save(context, currentUser.uid, finishedRide, rating, tipTk))
                coroutineScope.launch { firebaseRepo.saveRideRecord(finishedRide, tipTk) }
                // Reset clean state after payment
                pickupLocation = null
                destinationLocation = null
                routeInfo = null
                fareBreakdown = null
                currentRide = null
                simulatedDriverLocation = null
                appliedPromoCode = null
                appliedDiscountTk = 0
                passengerNote = ""
                rideState = RideState.IDLE
                showRidePlanner = false
            }
        )
    }

    BackHandler(enabled = showRidePlanner && !showLocationSearchSheet &&
        rideState in listOf(RideState.IDLE, RideState.ROUTE_PREVIEW)) {
        rideState = RideState.IDLE
        showRidePlanner = false
    }

    if (rideState == RideState.IDLE && !showRidePlanner) {
        RiderHomeScreen(
            modifier = screenModifier,
            user = currentUser,
            history = rideHistory,
            currentLocationLabel = pickupLocation?.name,
            isDarkTheme = isDarkTheme,
            onChooseVehicle = { type ->
                selectedVehicleType = type
                activeDriver = Driver.forVehicle(type)
                val pickup = pickupLocation
                showRidePlanner = true
                if (pickup != null && destinationLocation != null) {
                    calculateAndSetRoute(pickup, destinationLocation)
                    rideState = RideState.ROUTE_PREVIEW
                } else {
                    isSelectingPickup = pickup == null
                    showLocationSearchSheet = true
                }
            },
            onSearchDestination = {
                showRidePlanner = true
                isSelectingPickup = false
                showLocationSearchSheet = true
            },
            onChoosePickup = {
                showRidePlanner = true
                isSelectingPickup = true
                showLocationSearchSheet = true
            },
            onChooseSavedPlace = { place ->
                RecentPlacesStore.record(context, currentUser.uid, place)
                val pickup = pickupLocation
                calculateAndSetRoute(pickup, place)
                showRidePlanner = true
                if (pickup == null) {
                    isSelectingPickup = true
                    showLocationSearchSheet = true
                } else {
                    rideState = RideState.ROUTE_PREVIEW
                }
            },
            onTabSelected = { navigateToTab(it) }
        )
        return
    }

    Box(modifier = screenModifier.fillMaxSize()) {
        if (rideState == RideState.PERMISSION_REQUIRED) {
            LocationPermissionCard(
                onRequestPermission = onRequestLocationPermission,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Map Canvas
            if (mapCenter == null || !cameraInitialized) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))
            } else if (currentMapEngine == MapEngine.GEOAPIFY_MAP) {
                // Interactive Geoapify Street Map (Powered by Geoapify API)
                InteractiveStreetMap(
                    pickup = pickupLocation,
                    destination = destinationLocation,
                    routePoints = routeInfo?.pathPoints ?: emptyList(),
                    driverLocation = simulatedDriverLocation,
                    currentLocation = userCurrentLocation,
                    recenterRequest = recenterRequest,
                    vehicleType = selectedVehicleType,
                    isDarkTheme = isDarkTheme,
                    onMapClick = { onLocationClickedOnMap(it) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Google Maps View
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        compassEnabled = true,
                        myLocationButtonEnabled = false
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = hasLocationPermission && userCurrentLocation != null,
                        mapType = MapType.NORMAL
                    ),
                    onMapClick = { clickedLatLng ->
                        onLocationClickedOnMap(clickedLatLng)
                    }
                ) {
                    pickupLocation?.let { p ->
                        Marker(
                            state = stableMarkerState(p.latLng),
                            title = "Pickup: ${p.name}",
                            snippet = p.address,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        )
                    }

                    destinationLocation?.let { d ->
                        Marker(
                            state = stableMarkerState(d.latLng),
                            title = "Drop-off: ${d.name}",
                            snippet = d.address,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )
                    }

                    routeInfo?.pathPoints?.let { points ->
                        if (points.isNotEmpty()) {
                            Polyline(
                                points = points,
                                color = MaterialTheme.colorScheme.primary,
                                width = 14f
                            )
                        }
                    }

                    if (rideState in listOf(RideState.DRIVER_ON_WAY, RideState.DRIVER_AT_PICKUP,
                            RideState.TRIP_IN_PROGRESS) && simulatedDriverLocation != null) {
                        val markerHue = when (selectedVehicleType) {
                            VehicleType.BIKE -> BitmapDescriptorFactory.HUE_AZURE
                            VehicleType.CNG -> BitmapDescriptorFactory.HUE_CYAN
                            VehicleType.CAR -> BitmapDescriptorFactory.HUE_ROSE
                        }
                        Marker(
                            state = stableMarkerState(simulatedDriverLocation!!),
                            title = "${activeDriver.name} (${selectedVehicleType.title})",
                            snippet = "${activeDriver.vehicleModel} • ${activeDriver.plateNumber}",
                            icon = BitmapDescriptorFactory.defaultMarker(markerHue)
                        )
                    }
                }
            }

            // Floating Top App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Logo
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            if (rideState == RideState.IDLE || rideState == RideState.ROUTE_PREVIEW) {
                                IconButton(
                                    onClick = {
                                        rideState = RideState.IDLE
                                        showLocationSearchSheet = false
                                        showRidePlanner = false
                                    },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = tr("Back to home", "হোমে ফিরুন"),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(Modifier.width(4.dp))
                            }
                            BrandLogo(Modifier.size(38.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Uthen Bhai",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = tr("Your ride, made easy", "সহজে রাইড নিন"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (rideState == RideState.IDLE || rideState == RideState.ROUTE_PREVIEW) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable {
                                        rideState = RideState.IDLE
                                        showRidePlanner = false
                                        selectedTab = RiderTab.ACCOUNT
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = tr("Account", "অ্যাকাউন্ট"),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Floating Controls: Re-center & Swap
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, bottom = 220.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (hasLocationPermission) {
                            refreshCurrentLocation(autoCenter = true)
                        } else {
                            onRequestLocationPermission()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = tr("My location", "আমার অবস্থান"),
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (pickupLocation != null && destinationLocation != null &&
                    rideState in listOf(RideState.IDLE, RideState.ROUTE_PREVIEW)) {
                    FloatingActionButton(
                        onClick = {
                            calculateAndSetRoute(destinationLocation, pickupLocation)
                        },
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = tr("Swap pickup and destination", "পিকআপ ও গন্তব্য অদলবদল করুন"),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Bottom Sheets
            AnimatedContent(
                targetState = rideState,
                transitionSpec = {
                    (fadeIn(tween(180)) + slideInVertically(tween(180)) { it / 12 }) togetherWith
                        fadeOut(tween(100))
                },
                contentAlignment = Alignment.BottomCenter,
                label = "ride-sheet",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight * 0.72f)
                    .navigationBarsPadding()
                    .imePadding()
                    .align(Alignment.BottomCenter)
            ) { shownState ->
                when (shownState) {
                    RideState.IDLE -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 92.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                        ) {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
                                // Subtle top drag handle
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                                        .align(Alignment.CenterHorizontally)
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                if (mapCenter == null) {
                                    Text(
                                        if (locating) tr("Finding your location…", "আপনার অবস্থান খোঁজা হচ্ছে…")
                                        else tr("GPS position unavailable", "জিপিএস অবস্থান পাওয়া যায়নি"),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        tr("Search for an address or retry GPS. Your pickup stays empty until you choose it.",
                                            "ঠিকানা খুঁজুন অথবা জিপিএস আবার চেষ্টা করুন। বেছে নেওয়ার আগে পিকআপ ফাঁকা থাকবে।"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextButton(onClick = { refreshCurrentLocation(autoCenter = true) }, enabled = !locating) {
                                        Text(tr("Retry GPS", "জিপিএস আবার খুঁজুন"), fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Text(
                                    text = tr("Where to today?", "আজ কোথায় যাবেন?"),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tr("Choose your stops to see rides and fares",
                                        "রাইড ও ভাড়া দেখতে পিকআপ এবং গন্তব্য বেছে নিন"),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Connected Route Container
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        // Pickup row
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    isSelectingPickup = true
                                                    showLocationSearchSheet = true
                                                }
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(MaterialTheme.colorScheme.success, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = pickupLocation?.name ?:
                                                        tr("Select pickup location", "পিকআপ স্থান বেছে নিন"),
                                                    fontWeight = if (pickupLocation != null) FontWeight.SemiBold else FontWeight.Medium,
                                                    fontSize = 14.sp,
                                                    color = if (pickupLocation != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                if (pickupLocation == null) {
                                                    Text(
                                                        text = if (userCurrentLocation != null)
                                                            tr("GPS: ${userLocationAddress ?: "Current position ready"}",
                                                                "জিপিএস: ${userLocationAddress ?: "বর্তমান অবস্থান প্রস্তুত"}")
                                                        else tr("Tap map or search address", "মানচিত্রে চাপুন বা ঠিকানা খুঁজুন"),
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Vertical connector dots
                                        Row(
                                            modifier = Modifier.padding(start = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                repeat(3) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(2.dp)
                                                            .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(18.dp))
                                            HorizontalDivider(
                                                modifier = Modifier.weight(1f),
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                                thickness = 0.5.dp
                                            )
                                        }

                                        // Destination row
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    isSelectingPickup = false
                                                    showLocationSearchSheet = true
                                                }
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(OrangeAccent, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = destinationLocation?.name ?:
                                                        tr("Where to? Select destination", "কোথায় যাবেন? গন্তব্য বেছে নিন"),
                                                    fontWeight = if (destinationLocation != null) FontWeight.SemiBold else FontWeight.Medium,
                                                    fontSize = 14.sp,
                                                    color = if (destinationLocation != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                if (destinationLocation == null) {
                                                    Text(
                                                        text = tr("Tap map or search address", "মানচিত্রে চাপুন বা ঠিকানা খুঁজুন"),
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            Icon(
                                                imageVector = Icons.Default.NearMe,
                                                contentDescription = null,
                                                tint = OrangeAccent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                if (pickupLocation == null && userCurrentLocation != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .clickable {
                                                val currLoc = AppLocation(
                                                    id = "curr_loc",
                                                    name = "Current location",
                                                    address = userLocationAddress ?: String.format(Locale.US, "%.4f, %.4f", userCurrentLocation!!.latitude, userCurrentLocation!!.longitude),
                                                    latLng = userCurrentLocation!!
                                                )
                                                calculateAndSetRoute(currLoc, destinationLocation)
                                            }
                                            .heightIn(min = 44.dp)
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NearMe,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = tr("Use current location as pickup", "বর্তমান অবস্থান পিকআপ হিসেবে নিন"),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Preview Route CTA
                                val canCalculate = pickupLocation != null && destinationLocation != null
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (canCalculate) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                        )
                                        .clickable(enabled = canCalculate) {
                                            calculateAndSetRoute(pickupLocation, destinationLocation)
                                            rideState = RideState.ROUTE_PREVIEW
                                        }
                                        .padding(vertical = 15.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    VehicleIcon(
                                        vehicle = selectedVehicleType,
                                        tint = if (canCalculate) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (canCalculate) tr("See rides & fares", "রাইড ও ভাড়া দেখুন")
                                            else tr("Choose pickup and destination", "পিকআপ ও গন্তব্য বেছে নিন"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (canCalculate) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    RideState.ROUTE_PREVIEW -> {
                        if (pickupLocation != null && destinationLocation != null && routeInfo != null && fareBreakdown != null) {
                            RideBookingSheet(
                                pickup = pickupLocation!!,
                                destination = destinationLocation!!,
                                routeInfo = routeInfo!!,
                                selectedVehicle = selectedVehicleType,
                                quoteEpochMillis = quoteEpochMillis,
                                onSelectVehicle = { vType ->
                                    selectedVehicleType = vType
                                    activeDriver = Driver.forVehicle(vType)
                                    routeInfo = routeInfo!!.copy(durationMinutes =
                                        RouteHelper.estimatedDurationMinutes(routeInfo!!.distanceKm, vType))
                                    val updatedFare = FareCalculator.calculateFareWithPromo(
                                        routeInfo!!.distanceKm, routeInfo!!.durationMinutes,
                                        vType, appliedPromoCode, quoteEpochMillis)
                                    fareBreakdown = updatedFare
                                    appliedDiscountTk = updatedFare.discountTk
                                    if (appliedPromoCode != null && updatedFare.discountTk == 0) {
                                        appliedPromoCode = null
                                    }
                                    requestRoadRoute(pickupLocation!!, destinationLocation!!)
                                },
                                fareBreakdown = fareBreakdown!!,
                                routeNotice = when (routeQuality) {
                                    RouteQuality.CHECKING -> tr("Checking the road route before booking…",
                                        "বুকিংয়ের আগে সড়কপথ যাচাই করা হচ্ছে…")
                                    RouteQuality.ESTIMATED -> tr("This route and fare use an estimated distance.",
                                        "এই পথ ও ভাড়া আনুমানিক দূরত্বের ভিত্তিতে দেখানো হচ্ছে।")
                                    RouteQuality.ROAD -> tr("Road route loaded. Review the updated fare below.",
                                        "সড়কপথ পাওয়া গেছে। নিচে নতুন ভাড়া দেখুন।")
                                    RouteQuality.NONE -> ""
                                },
                                routeLoading = routeQuality == RouteQuality.CHECKING,
                                canRetryRoute = routeQuality == RouteQuality.ESTIMATED &&
                                    GeoapifyService.apiKey.isNotBlank(),
                                onRetryRoute = { requestRoadRoute(pickupLocation!!, destinationLocation!!) },
                                paymentMode = selectedPaymentMode,
                                onSelectPaymentMode = { selectedPaymentMode = it },
                                promoCode = appliedPromoCode,
                                discountTk = appliedDiscountTk,
                                onApplyPromo = { code ->
                                    val updatedFare = FareCalculator.calculateFareWithPromo(
                                        routeInfo!!.distanceKm, routeInfo!!.durationMinutes,
                                        selectedVehicleType, code, quoteEpochMillis)
                                    if (updatedFare.discountTk > 0) {
                                        appliedPromoCode = code.trim().uppercase()
                                        appliedDiscountTk = updatedFare.discountTk
                                        fareBreakdown = updatedFare
                                        true
                                    } else {
                                        false
                                    }
                                },
                                noteToDriver = passengerNote,
                                onUpdateNote = { passengerNote = it },
                                onChangePickup = {
                                    isSelectingPickup = true
                                    showLocationSearchSheet = true
                                },
                                onChangeDestination = {
                                    isSelectingPickup = false
                                    showLocationSearchSheet = true
                                },
                                onConfirmBooking = {
                                    activeDriver = Driver.forVehicle(selectedVehicleType)
                                    currentRide = ActiveRide(
                                        pickup = pickupLocation!!,
                                        destination = destinationLocation!!,
                                        vehicleType = selectedVehicleType,
                                        fareBreakdown = fareBreakdown!!,
                                        driver = activeDriver,
                                        paymentMode = selectedPaymentMode,
                                        promoCode = appliedPromoCode,
                                        discountTk = appliedDiscountTk,
                                        passengerNote = passengerNote,
                                        state = RideState.SEARCHING_DRIVER
                                    )
                                    rideState = RideState.SEARCHING_DRIVER
                                }
                            )
                        }
                    }

                    RideState.SEARCHING_DRIVER -> {
                        DriverMatchingOverlay(
                            vehicleType = selectedVehicleType,
                            onCancel = { showCancelDialog = true }
                        )
                    }

                    RideState.MATCH_UNAVAILABLE -> {
                        MatchUnavailableSheet(
                            reason = if (outsideDemoArea)
                                tr("No demo drivers are available near this pickup. Choose a pickup closer to Dhaka.",
                                    "এই পিকআপের কাছে ডেমো চালক নেই। ঢাকার কাছাকাছি পিকআপ বেছে নিন।")
                            else tr("Your device appears offline. Connect and try again, or edit your trip.",
                                "আপনার ডিভাইস অফলাইন। সংযোগ দিয়ে আবার চেষ্টা করুন অথবা রাইড বদলান।"),
                            onRetry = { rideState = RideState.SEARCHING_DRIVER },
                            onEditRide = {
                                currentRide = null
                                rideState = RideState.ROUTE_PREVIEW
                            }
                        )
                    }

                    RideState.DRIVER_AT_PICKUP -> {
                        currentRide?.let { ride ->
                            PickupVerificationSheet(
                                ride = ride,
                                onVerified = { rideState = RideState.TRIP_IN_PROGRESS },
                                onCancel = { showCancelDialog = true }
                            )
                        }
                    }

                    RideState.DRIVER_ON_WAY, RideState.TRIP_IN_PROGRESS -> {
                        if (currentRide != null) {
                            ActiveTripSheet(
                                ride = currentRide!!.copy(state = shownState),
                                onCancelRide = { showCancelDialog = true },
                                onCompleteTrip = {
                                    rideState = RideState.PAYMENT_DUE
                                }
                            )
                        }
                    }

                    else -> Unit
                }
            }

            if (rideState == RideState.IDLE && !showLocationSearchSheet) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    RiderNavigationBar(selected = RiderTab.RIDE, onSelect = { navigateToTab(it) })
                }
            }

            if (showCancelDialog) {
                CancelRideDialog(
                    onDismiss = { showCancelDialog = false },
                    onConfirm = { reason ->
                        currentRide?.let { ride ->
                            rideHistory.add(0, RideHistoryStore.saveCancelled(
                                context, currentUser.uid, ride, reason))
                        }
                        showCancelDialog = false
                        simulatedDriverLocation = null
                        currentRide = null
                        rideState = RideState.ROUTE_PREVIEW
                    }
                )
            }

            // Location Search Bottom Sheet modal
            if (showLocationSearchSheet) {
                CustomLocationPickerSheet(
                    title = if (isSelectingPickup) tr("Choose pickup location", "পিকআপ স্থান বেছে নিন")
                        else tr("Choose destination", "গন্তব্য বেছে নিন"),
                    userId = currentUser.uid,
                    currentLocation = userCurrentLocation,
                    homeAddress = SavedPlacesStore.load(context, currentUser.uid, "home"),
                    workAddress = SavedPlacesStore.load(context, currentUser.uid, "work"),
                    onEditSavedPlaces = {
                        showLocationSearchSheet = false
                        showRidePlanner = false
                        rideState = RideState.IDLE
                        selectedTab = RiderTab.ACCOUNT
                    },
                    onSelectLocation = { selected ->
                        if (isSelectingPickup) {
                            calculateAndSetRoute(selected, destinationLocation)
                        } else {
                            calculateAndSetRoute(pickupLocation, selected)
                        }
                        showLocationSearchSheet = false
                        if (pickupLocation != null && destinationLocation != null) {
                            rideState = RideState.ROUTE_PREVIEW
                        }
                    },
                    onPickFromMap = {
                        showLocationSearchSheet = false
                        // User can tap directly on the map
                    },
                    onDismiss = { showLocationSearchSheet = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
