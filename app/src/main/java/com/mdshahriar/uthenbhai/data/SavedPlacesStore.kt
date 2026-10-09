package com.mdshahriar.uthenbhai.data

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.mdshahriar.uthenbhai.model.AppLocation
import org.json.JSONObject

object SavedPlacesStore {
    private const val PREFS = "saved_places"

    fun load(context: Context, userId: String, slot: String): AppLocation? = runCatching {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("${userId}_$slot", null) ?: return@runCatching null
        val item = JSONObject(raw)
        AppLocation(
            id = "saved_$slot",
            name = slot.replaceFirstChar { it.uppercase() },
            address = item.getString("address"),
            latLng = LatLng(item.getDouble("lat"), item.getDouble("lng"))
        )
    }.getOrNull()

    fun save(context: Context, userId: String, slot: String, place: AppLocation?) {
        val value = place?.let {
            JSONObject().apply {
                put("address", it.address)
                put("lat", it.latitude)
                put("lng", it.longitude)
            }.toString()
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString("${userId}_$slot", value).apply()
    }
}
