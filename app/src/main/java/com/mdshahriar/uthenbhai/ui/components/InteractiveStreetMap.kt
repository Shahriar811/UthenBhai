package com.mdshahriar.uthenbhai.ui.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.mdshahriar.uthenbhai.BuildConfig
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.VehicleType
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun InteractiveStreetMap(
    pickup: AppLocation?,
    destination: AppLocation?,
    routePoints: List<LatLng>,
    driverLocation: LatLng?,
    onMapClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier,
    currentLocation: LatLng? = null,
    recenterRequest: Int = 0,
    vehicleType: VehicleType = VehicleType.BIKE,
    isDarkTheme: Boolean = false
) {
    // A map is never displayed at a preset location while GPS is unavailable.
    val initialPosition = pickup?.latLng ?: destination?.latLng ?: currentLocation ?: return
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val routeColor = MaterialTheme.colorScheme.primary.toArgb()
    val tileSource = remember(isDarkTheme) {
        val style = if (isDarkTheme) "dark-matter" else "osm-bright"
        val hasKey = BuildConfig.GEOAPIFY_API_KEY.isNotBlank()
        object : OnlineTileSourceBase(if (hasKey) "Geoapify-$style" else "OpenStreetMap",
            0, if (hasKey) 20 else 19, 256, ".png", arrayOf("https://tile.openstreetmap.org/")) {
            override fun getTileURLString(index: Long): String {
                val path = "${MapTileIndex.getZoom(index)}/${MapTileIndex.getX(index)}/${MapTileIndex.getY(index)}.png"
                return if (hasKey) "https://maps.geoapify.com/v1/tile/$style/$path?apiKey=${BuildConfig.GEOAPIFY_API_KEY}"
                else "https://tile.openstreetmap.org/$path"
            }
        }
    }
    fun dot(color: Int, sizeDp: Int) = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
        setStroke((2 * density).toInt(), android.graphics.Color.WHITE)
        val size = (sizeDp * density).toInt()
        setSize(size, size)
    }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var lastEndpoints by remember { mutableStateOf<Pair<LatLng?, LatLng?>?>(null) }
    var lastRecenter by remember { mutableStateOf(recenterRequest) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapViewRef?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapViewRef?.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapViewRef?.onDetach()
            mapViewRef = null
        }
    }
    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            Configuration.getInstance().userAgentValue = "UthenBhai/1.0 (com.mdshahriar.uthenbhai)"
            MapView(ctx).apply {
                setTileSource(tileSource)
                setMultiTouchControls(true)
                zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                isTilesScaledToDpi = true
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(initialPosition.latitude, initialPosition.longitude))
                overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
                        point ?: return false
                        currentOnMapClick(LatLng(point.latitude, point.longitude))
                        return true
                    }
                    override fun longPressHelper(point: GeoPoint?) = false
                }))
                onResume()
                mapViewRef = this
            }
        },
        update = { map ->
            if (map.tileProvider.tileSource.name() != tileSource.name()) map.setTileSource(tileSource)
            val tapOverlay = map.overlays.firstOrNull { it is MapEventsOverlay }
            map.overlays.clear()
            tapOverlay?.let { map.overlays.add(it) }
            if (routePoints.isNotEmpty()) {
                map.overlays.add(Polyline(map).apply {
                    outlinePaint.color = routeColor
                    outlinePaint.strokeWidth = 4f * density
                    outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                    setPoints(routePoints.map { GeoPoint(it.latitude, it.longitude) })
                })
            }
            fun addMarker(point: LatLng, titleText: String, address: String, color: Int, size: Int = 18) {
                map.overlays.add(Marker(map).apply {
                    position = GeoPoint(point.latitude, point.longitude)
                    icon = dot(color, size)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    title = titleText
                    snippet = address
                })
            }
            pickup?.let { addMarker(it.latLng, "Pickup: ${it.name}", it.address, 0xFF16866A.toInt()) }
            destination?.let { addMarker(it.latLng, "Destination: ${it.name}", it.address, 0xFFBB5100.toInt()) }
            // This is the measured GPS dot, never an inferred pickup pin.
            currentLocation?.let { addMarker(it, "Your location", "GPS", 0xFF1976D2.toInt(), 12) }
            driverLocation?.let { addMarker(it, vehicleType.title, "Driver", routeColor, 22) }
            val endpoints = pickup?.latLng to destination?.latLng
            if (recenterRequest != lastRecenter && currentLocation != null) {
                map.controller.animateTo(GeoPoint(currentLocation.latitude, currentLocation.longitude))
                lastRecenter = recenterRequest
            } else if (endpoints != lastEndpoints) {
                lastEndpoints = endpoints
                if (pickup != null && destination != null) {
                    val points = routePoints.ifEmpty { listOf(pickup.latLng, destination.latLng) }
                    val bounds = BoundingBox.fromGeoPoints(points.map { GeoPoint(it.latitude, it.longitude) })
                    map.post { if (mapViewRef === map) map.zoomToBoundingBox(bounds, true, 100) }
                } else {
                    val point = pickup?.latLng ?: destination?.latLng
                    point?.let { map.controller.animateTo(GeoPoint(it.latitude, it.longitude)) }
                }
            }
            map.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}
