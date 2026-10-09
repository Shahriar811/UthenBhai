package com.mdshahriar.uthenbhai.data

import android.content.Context
import com.mdshahriar.uthenbhai.model.ActiveRide
import org.json.JSONArray
import org.json.JSONObject

data class RideHistoryEntry(
    val id: String,
    val pickup: String,
    val destination: String,
    val vehicle: String,
    val driver: String,
    val fareTk: Int,
    val tipTk: Int,
    val timestamp: Long,
    val rating: Int,
    val status: String = "completed",
    val cancelReason: String? = null
)

/** A small local history so the portfolio flow works without a Firebase project. */
object RideHistoryStore {
    private const val PREFS = "rider_history"

    fun load(context: Context, userId: String): List<RideHistoryEntry> = runCatching {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(userId, "[]") ?: "[]"
        val array = JSONArray(raw)
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            RideHistoryEntry(
                id = item.getString("id"),
                pickup = item.getString("pickup"),
                destination = item.getString("destination"),
                vehicle = item.getString("vehicle"),
                driver = item.getString("driver"),
                fareTk = item.getInt("fareTk"),
                tipTk = item.optInt("tipTk", 0),
                timestamp = item.getLong("timestamp"),
                rating = item.optInt("rating", 0),
                status = item.optString("status", "completed"),
                cancelReason = item.optString("cancelReason").ifBlank { null }
            )
        }.sortedByDescending { it.timestamp }
    }.getOrDefault(emptyList())

    fun save(context: Context, userId: String, ride: ActiveRide, rating: Int, tipTk: Int): RideHistoryEntry {
        val entry = RideHistoryEntry(
            id = ride.id,
            pickup = ride.pickup.name,
            destination = ride.destination.name,
            vehicle = ride.vehicleType.title,
            driver = ride.driver.name,
            fareTk = ride.fareBreakdown.totalFareTk,
            tipTk = tipTk,
            timestamp = System.currentTimeMillis(),
            rating = rating
        )
        write(context, userId, entry)
        return entry
    }

    fun saveCancelled(context: Context, userId: String, ride: ActiveRide,
                      reason: String): RideHistoryEntry {
        val entry = RideHistoryEntry(
            id = ride.id,
            pickup = ride.pickup.name,
            destination = ride.destination.name,
            vehicle = ride.vehicleType.title,
            driver = ride.driver.name,
            fareTk = 0,
            tipTk = 0,
            timestamp = System.currentTimeMillis(),
            rating = 0,
            status = "cancelled",
            cancelReason = reason
        )
        write(context, userId, entry)
        return entry
    }

    private fun write(context: Context, userId: String, entry: RideHistoryEntry) {
        val existing = load(context, userId).filterNot { it.id == entry.id }
        val array = JSONArray()
        (listOf(entry) + existing).take(50).forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("pickup", item.pickup)
                put("destination", item.destination)
                put("vehicle", item.vehicle)
                put("driver", item.driver)
                put("fareTk", item.fareTk)
                put("tipTk", item.tipTk)
                put("timestamp", item.timestamp)
                put("rating", item.rating)
                put("status", item.status)
                put("cancelReason", item.cancelReason)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(userId, array.toString()).apply()
    }
}
