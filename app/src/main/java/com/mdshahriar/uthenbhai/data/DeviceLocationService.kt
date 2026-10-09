package com.mdshahriar.uthenbhai.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

object DeviceLocationService {
    /** A fresh foreground fix; never substitutes a saved or preset position. */
    @SuppressLint("MissingPermission")
    suspend fun currentPosition(context: Context): LatLng? {
        val cancellation = CancellationTokenSource()
        return try {
            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(0L)
                .setDurationMillis(20000L)
                .build()
            withTimeoutOrNull(25000L) {
                LocationServices.getFusedLocationProviderClient(context)
                    .getCurrentLocation(request, cancellation.token).await()?.let {
                        LatLng(it.latitude, it.longitude)
                    }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        } finally {
            cancellation.cancel()
        }
    }

    suspend fun address(context: Context, point: LatLng): String = withContext(Dispatchers.IO) {
        val online = GeoapifyService.reverseGeocode(point.latitude, point.longitude)
        if (!online.isNullOrBlank()) return@withContext online
        val geocoded = runCatching {
            if (!Geocoder.isPresent()) return@runCatching null
            @Suppress("DEPRECATION")
            Geocoder(context, Locale.getDefault()).getFromLocation(point.latitude, point.longitude, 1)
                ?.firstOrNull()?.getAddressLine(0)
        }.getOrNull()
        geocoded?.takeIf { it.isNotBlank() }
            ?: String.format(Locale.US, "%.4f, %.4f", point.latitude, point.longitude)
    }
}
