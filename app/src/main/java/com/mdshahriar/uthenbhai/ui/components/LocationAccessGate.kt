package com.mdshahriar.uthenbhai.ui.components

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** Applies to login as well as the rider screens, and rechecks access on resume. */
@Composable
fun LocationAccessGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    fun hasPreciseAccess() = ContextCompat.checkSelfPermission(context,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    fun isGpsEnabled() = runCatching {
        context.getSystemService(LocationManager::class.java)
            ?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
    }.getOrDefault(false)
    var preciseAccess by remember { mutableStateOf(hasPreciseAccess()) }
    var gpsEnabled by remember { mutableStateOf(isGpsEnabled()) }
    var permissionRequested by rememberSaveable { mutableStateOf(false) }
    var gpsPrompted by rememberSaveable { mutableStateOf(false) }
    var checkingSettings by remember { mutableStateOf(false) }
    fun refresh() {
        preciseAccess = hasPreciseAccess()
        gpsEnabled = isGpsEnabled()
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { refresh() }
    val gpsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()) { refresh() }
    fun openAppSettings() {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context.packageName}")))
    }
    fun requestPermission() {
        permissionRequested = true
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    fun requestGps() {
        if (checkingSettings) return
        gpsPrompted = true
        checkingSettings = true
        scope.launch {
            try {
                val request = LocationSettingsRequest.Builder()
                    .addLocationRequest(LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L).build())
                    .setAlwaysShow(true).build()
                LocationServices.getSettingsClient(context).checkLocationSettings(request).await()
                refresh()
                if (!gpsEnabled) context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (error is ResolvableApiException) {
                    gpsLauncher.launch(IntentSenderRequest.Builder(error.resolution).build())
                } else context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            } finally {
                checkingSettings = false
            }
        }
    }
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refresh()
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) = refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        ContextCompat.registerReceiver(context, receiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.unregisterReceiver(receiver)
        }
    }
    LaunchedEffect(Unit) {
        if (!preciseAccess && !permissionRequested) requestPermission()
    }
    LaunchedEffect(preciseAccess, gpsEnabled) {
        if (preciseAccess && !gpsEnabled && !gpsPrompted) requestGps()
    }
    when {
        !preciseAccess -> LocationPermissionCard(
            onRequestPermission = {
                if (permissionRequested && !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity, Manifest.permission.ACCESS_FINE_LOCATION)) openAppSettings()
                else requestPermission()
            },
            onOpenSettings = ::openAppSettings
        )
        !gpsEnabled -> LocationPermissionCard(
            onRequestPermission = ::requestGps,
            needsGps = true,
            isLoading = checkingSettings
        )
        else -> content()
    }
}
