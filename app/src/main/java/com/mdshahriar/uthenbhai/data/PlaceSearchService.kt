package com.mdshahriar.uthenbhai.data

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mdshahriar.uthenbhai.BuildConfig
import com.mdshahriar.uthenbhai.model.AppLocation
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class PlaceSuggestion(
    val id: String,
    val title: String,
    val subtitle: String,
    val placeId: String? = null,
    val location: AppLocation? = null,
    val isGoogleResult: Boolean = false
)

data class PlaceSearchResult(
    val suggestions: List<PlaceSuggestion>,
    val onlineUnavailable: Boolean = false
)

/** Google Places predictions first, with existing geocoding and device lookup as fallbacks. */
object PlaceSearchService {
    private const val TAG = "PlaceSearchService"
    private var placesClient: PlacesClient? = null

    private fun googleClient(context: Context): PlacesClient? {
        val key = BuildConfig.MAPS_API_KEY
        if (key.isBlank()) return null
        return synchronized(this) {
            placesClient ?: runCatching {
                Places.initializeWithNewPlacesApiEnabled(context.applicationContext, key)
                Places.createClient(context.applicationContext).also { placesClient = it }
            }.getOrElse {
                Log.w(TAG, "Places initialization failed: ${it.javaClass.simpleName}")
                null
            }
        }
    }

    suspend fun search(
        context: Context,
        query: String,
        near: LatLng?,
        sessionToken: AutocompleteSessionToken
    ): PlaceSearchResult {
        val cleanQuery = query.trim()
        if (cleanQuery.length < 3) return PlaceSearchResult(emptyList())
        val client = googleClient(context)
        if (client != null) {
            try {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(cleanQuery)
                    .setCountries(listOf("BD"))
                    .apply { near?.let { setLocationBias(CircularBounds.newInstance(it, 50000.0)) } }
                    .setSessionToken(sessionToken)
                    .build()
                val predictions = client.findAutocompletePredictions(request).await()
                    .autocompletePredictions
                return PlaceSearchResult(predictions.map { prediction ->
                    PlaceSuggestion(
                        id = "google_${prediction.placeId}",
                        title = prediction.getPrimaryText(null).toString(),
                        subtitle = prediction.getSecondaryText(null).toString(),
                        placeId = prediction.placeId,
                        isGoogleResult = true
                    )
                })
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(TAG, "Google suggestions unavailable: ${error.javaClass.simpleName}")
            }
        }

        if (GeoapifyService.apiKey.isNotBlank()) {
            val results = GeoapifyService.searchPlaces(cleanQuery, near)
            if (results.isNotEmpty()) {
                return PlaceSearchResult(results.map { place ->
                    PlaceSuggestion(place.id, place.name, place.address, location = place)
                })
            }
        }

        val deviceResults = withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) return@withContext emptyList<AppLocation>()
                @Suppress("DEPRECATION")
                val addresses = Geocoder(context, Locale.getDefault())
                    .getFromLocationName(cleanQuery, 6, 20.6, 88.0, 26.7, 92.7).orEmpty()
                addresses.mapNotNull { address ->
                    val lat = address.latitude
                    val lon = address.longitude
                    if (!lat.isFinite() || !lon.isFinite()) null else AppLocation(
                        id = "device_${lat}_${lon}",
                        name = address.featureName?.takeIf { it.isNotBlank() }
                            ?: address.locality?.takeIf { it.isNotBlank() } ?: cleanQuery,
                        address = address.getAddressLine(0) ?: cleanQuery,
                        latLng = LatLng(lat, lon)
                    )
                }
            } catch (error: Exception) {
                Log.w(TAG, "Device geocoder unavailable: ${error.javaClass.simpleName}")
                emptyList()
            }
        }
        return PlaceSearchResult(deviceResults.map { place ->
            PlaceSuggestion(place.id, place.name, place.address, location = place)
        }, onlineUnavailable = deviceResults.isEmpty())
    }

    suspend fun resolve(
        context: Context,
        suggestion: PlaceSuggestion,
        sessionToken: AutocompleteSessionToken
    ): AppLocation? {
        suggestion.location?.let { return it }
        val placeId = suggestion.placeId ?: return null
        val client = googleClient(context) ?: return null
        return try {
            val request = FetchPlaceRequest.builder(placeId,
                listOf(Place.Field.ID, Place.Field.LOCATION, Place.Field.FORMATTED_ADDRESS))
                .setSessionToken(sessionToken)
                .build()
            val place = client.fetchPlace(request).await().place
            val point = place.location ?: return null
            AppLocation(
                id = place.id ?: placeId,
                name = suggestion.title,
                address = place.formattedAddress ?: suggestion.subtitle,
                latLng = point
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Log.w(TAG, "Place details unavailable: ${error.javaClass.simpleName}")
            null
        }
    }
}
