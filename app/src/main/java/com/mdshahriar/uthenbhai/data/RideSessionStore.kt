package com.mdshahriar.uthenbhai.data

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.RideState
import com.mdshahriar.uthenbhai.model.VehicleType
import org.json.JSONObject
import org.json.JSONArray
import com.mdshahriar.uthenbhai.util.isActiveJourney
import com.mdshahriar.uthenbhai.util.shouldRestoreJourney

data class RideSession(
    val pickup: AppLocation?,
    val destination: AppLocation?,
    val vehicle: VehicleType,
    val state: RideState,
    val rideId: String?,
    val routeDistanceKm: Double?,
    val routeDurationMinutes: Int?,
    val quoteEpochMillis: Long,
    val promoCode: String?,
    val discountTk: Int,
    val note: String,
    val routePoints: List<LatLng> = emptyList(),
    val driverPosition: LatLng? = null,
    val nextRoutePoint: Int = 0
)

/** Persists the current demo journey independently of Activity and Compose state. */
object RideSessionStore {
    private const val PREFS = "ride_sessions"

    fun load(context: Context, userId: String): RideSession? = runCatching {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(userId, null) ?: return@runCatching null
        val item = JSONObject(raw)
        val state = runCatching { RideState.valueOf(item.getString("state")) }
            .getOrDefault(RideState.IDLE)
        RideSession(
            pickup = readPlace(item.optJSONObject("pickup")),
            destination = readPlace(item.optJSONObject("destination")),
            vehicle = runCatching { VehicleType.valueOf(item.getString("vehicle")) }
                .getOrDefault(VehicleType.BIKE),
            state = state,
            rideId = item.optString("rideId").ifBlank { null },
            routeDistanceKm = item.optDouble("routeDistanceKm", 0.0).takeIf { it > 0.0 },
            routeDurationMinutes = item.optInt("routeDurationMinutes", 0).takeIf { it > 0 },
            quoteEpochMillis = item.optLong("quoteEpochMillis", 0L)
                .takeIf { it > 0L } ?: System.currentTimeMillis(),
            promoCode = item.optString("promoCode").ifBlank { null },
            discountTk = item.optInt("discountTk", 0),
            note = item.optString("note"),
            routePoints = item.optJSONArray("routePoints")?.let { points ->
                List(points.length()) { index ->
                    val point = points.getJSONArray(index)
                    LatLng(point.getDouble(0), point.getDouble(1))
                }
            }.orEmpty(),
            driverPosition = item.optJSONObject("driverPosition")?.let {
                LatLng(it.getDouble("lat"), it.getDouble("lng"))
            },
            nextRoutePoint = item.optInt("nextRoutePoint", 0).coerceAtLeast(0)
        )
    }.getOrNull()?.let { session ->
        if (shouldRestoreJourney(session.state, session.pickup != null,
                session.destination != null, !session.rideId.isNullOrBlank())) session
        else {
            clear(context, userId)
            null
        }
    }

    fun save(context: Context, userId: String, session: RideSession) {
        if (!session.state.isActiveJourney) {
            clear(context, userId)
            return
        }
        val value = JSONObject().apply {
            put("pickup", writePlace(session.pickup))
            put("destination", writePlace(session.destination))
            put("vehicle", session.vehicle.name)
            put("state", session.state.name)
            put("rideId", session.rideId)
            put("routeDistanceKm", session.routeDistanceKm)
            put("routeDurationMinutes", session.routeDurationMinutes)
            put("quoteEpochMillis", session.quoteEpochMillis)
            put("promoCode", session.promoCode)
            put("discountTk", session.discountTk)
            put("note", session.note)
            put("routePoints", JSONArray().apply {
                session.routePoints.forEach { put(JSONArray().put(it.latitude).put(it.longitude)) }
            })
            put("driverPosition", session.driverPosition?.let {
                JSONObject().put("lat", it.latitude).put("lng", it.longitude)
            })
            put("nextRoutePoint", session.nextRoutePoint)
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(userId, value.toString()).apply()
    }

    fun clear(context: Context, userId: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove(userId).apply()
    }

    private fun writePlace(place: AppLocation?): JSONObject? = place?.let {
        JSONObject().apply {
            put("id", it.id)
            put("name", it.name)
            put("address", it.address)
            put("lat", it.latitude)
            put("lng", it.longitude)
        }
    }

    private fun readPlace(item: JSONObject?): AppLocation? = item?.let {
        AppLocation(
            id = it.optString("id"),
            name = it.getString("name"),
            address = it.getString("address"),
            latLng = LatLng(it.getDouble("lat"), it.getDouble("lng"))
        )
    }
}
