package com.mdshahriar.uthenbhai.data

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.mdshahriar.uthenbhai.BuildConfig
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.RouteInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.math.roundToInt

object GeoapifyService {

    private const val TAG = "GeoapifyService"

    val apiKey: String
        get() = BuildConfig.GEOAPIFY_API_KEY

    /**
     * Reverse Geocoding: Converts LatLng to human-readable street address using Geoapify
     */
    suspend fun reverseGeocode(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null
        try {
            val urlString = "https://api.geoapify.com/v1/geocode/reverse?lat=$lat&lon=$lon&format=json&apiKey=$apiKey"
            val response = executeGetRequest(urlString) ?: return@withContext null
            val json = JSONObject(response)
            val results = json.optJSONArray("results")
            if (results != null && results.length() > 0) {
                val first = results.getJSONObject(0)
                val formatted = first.optString("formatted")
                val addressLine1 = first.optString("address_line1")
                val addressLine2 = first.optString("address_line2")

                when {
                    addressLine1.isNotBlank() && addressLine2.isNotBlank() -> "$addressLine1, $addressLine2"
                    formatted.isNotBlank() -> formatted
                    else -> addressLine1
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "reverseGeocode error: ${e.message}")
            null
        }
    }

    /**
     * Autocomplete search: queries Geoapify for place suggestions in Bangladesh
     */
    suspend fun searchPlaces(query: String, near: LatLng? = null): List<AppLocation> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || query.isBlank()) return@withContext emptyList()
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val proximity = near?.let { "&bias=proximity:${it.longitude},${it.latitude}" }.orEmpty()
            val urlString = "https://api.geoapify.com/v1/geocode/autocomplete?text=$encodedQuery&filter=countrycode:bd$proximity&limit=7&apiKey=$apiKey"
            val response = executeGetRequest(urlString) ?: return@withContext emptyList()
            val json = JSONObject(response)
            val features = json.optJSONArray("features") ?: return@withContext emptyList()

            val locations = mutableListOf<AppLocation>()
            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val properties = feature.optJSONObject("properties") ?: continue
                val geometry = feature.optJSONObject("geometry") ?: continue
                val coords = geometry.optJSONArray("coordinates") ?: continue

                val lon = coords.optDouble(0)
                val lat = coords.optDouble(1)
                val name = properties.optString("name").ifBlank {
                    properties.optString("street").ifBlank {
                        properties.optString("address_line1", query)
                    }
                }
                val address = properties.optString("formatted").ifBlank {
                    properties.optString("address_line2", "Dhaka, Bangladesh")
                }
                val placeId = properties.optString("place_id", "geoapify_$i")

                locations.add(
                    AppLocation(
                        id = placeId,
                        name = name,
                        address = address,
                        latLng = LatLng(lat, lon)
                    )
                )
            }
            locations
        } catch (e: Exception) {
            Log.w(TAG, "searchPlaces error: ${e.message}")
            emptyList()
        }
    }

    /**
     * Routing: Queries Geoapify Routing API for real turn-by-turn road polyline
     */
    suspend fun fetchRoute(
        pickup: AppLocation,
        destination: AppLocation,
        mode: String = "motorcycle" // motorcycle, drive, bicycle
    ): RouteInfo? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null
        try {
            val fromLat = pickup.latitude
            val fromLon = pickup.longitude
            val toLat = destination.latitude
            val toLon = destination.longitude

            val urlString = "https://api.geoapify.com/v1/routing?waypoints=$fromLat,$fromLon|$toLat,$toLon&mode=$mode&apiKey=$apiKey"
            val response = executeGetRequest(urlString) ?: return@withContext null
            val json = JSONObject(response)
            val features = json.optJSONArray("features") ?: return@withContext null
            if (features.length() == 0) return@withContext null

            val first = features.getJSONObject(0)
            val properties = first.optJSONObject("properties") ?: return@withContext null
            val distanceMeters = properties.optDouble("distance", 0.0)
            val timeSeconds = properties.optDouble("time", 0.0)

            val distanceKm = (distanceMeters / 100.0).roundToInt() / 10.0 // 1 decimal
            val durationMins = (timeSeconds / 60.0).roundToInt().coerceAtLeast(2)

            val geometry = first.optJSONObject("geometry") ?: return@withContext null
            val coordinates = geometry.optJSONArray("coordinates") ?: return@withContext null

            val points = mutableListOf<LatLng>()
            // Geometry can be LineString or MultiLineString
            val type = geometry.optString("type")
            if (type == "LineString") {
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    val lon = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    points.add(LatLng(lat, lon))
                }
            } else if (type == "MultiLineString") {
                for (i in 0 until coordinates.length()) {
                    val line = coordinates.getJSONArray(i)
                    for (j in 0 until line.length()) {
                        val coord = line.getJSONArray(j)
                        val lon = coord.getDouble(0)
                        val lat = coord.getDouble(1)
                        points.add(LatLng(lat, lon))
                    }
                }
            }

            if (points.isNotEmpty()) {
                RouteInfo(
                    pickup = pickup,
                    destination = destination,
                    distanceKm = distanceKm.coerceAtLeast(0.5),
                    durationMinutes = durationMins,
                    pathPoints = points
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "fetchRoute error: ${e.message}")
            null
        }
    }

    private fun executeGetRequest(urlString: String): String? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "UthenBhai/1.0 (Android; Geoapify)")
            }

            if (connection.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val sb = java.lang.StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                reader.close()
                sb.toString()
            } else {
                Log.w(TAG, "HTTP error: ${connection.responseCode} - ${connection.responseMessage}")
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "executeGetRequest exception: ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }
}
