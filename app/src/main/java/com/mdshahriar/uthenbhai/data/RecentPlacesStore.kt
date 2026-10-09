package com.mdshahriar.uthenbhai.data

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.mdshahriar.uthenbhai.model.AppLocation
import org.json.JSONArray
import org.json.JSONObject

/** The five most recently selected search results for each local profile. */
object RecentPlacesStore {
    private const val PREFS = "recent_place_searches"
    private const val LIMIT = 5

    fun load(context: Context, userId: String): List<AppLocation> = runCatching {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(userId, "[]") ?: "[]"
        val items = JSONArray(raw)
        (0 until items.length()).mapNotNull { index ->
            runCatching {
                val item = items.getJSONObject(index)
                val lat = item.getDouble("lat")
                val lng = item.getDouble("lng")
                require(lat.isFinite() && lng.isFinite() && lat in -90.0..90.0 && lng in -180.0..180.0)
                AppLocation(item.getString("id"), item.getString("name"),
                    item.getString("address"), LatLng(lat, lng))
            }.getOrNull()
        }.take(LIMIT)
    }.getOrDefault(emptyList())

    @Synchronized
    fun record(context: Context, userId: String, place: AppLocation) {
        // Home and Work are separate shortcuts rather than recent searches.
        if (place.id.startsWith("saved_")) return
        val places = (listOf(place) + load(context, userId).filterNot {
            it.id == place.id || (it.latitude == place.latitude && it.longitude == place.longitude)
        }).take(LIMIT)
        val items = JSONArray()
        places.forEach {
            items.put(JSONObject().apply {
                put("id", it.id)
                put("name", it.name)
                put("address", it.address)
                put("lat", it.latitude)
                put("lng", it.longitude)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(userId, items.toString()).apply()
    }
}
