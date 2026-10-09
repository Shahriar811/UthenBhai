package com.mdshahriar.uthenbhai.data

import android.content.Context
import com.mdshahriar.uthenbhai.model.UserProfile
import org.json.JSONObject

/** Remembers the selected portfolio profile on this device. It is not authentication. */
object ProfileSessionStore {
    private const val PREFS = "profile_session"
    private const val KEY = "current_profile"

    fun load(context: Context): UserProfile? = runCatching {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return@runCatching null
        val item = JSONObject(raw)
        UserProfile(
            uid = item.getString("uid"),
            name = item.getString("name"),
            email = item.optString("email"),
            phone = item.optString("phone")
        )
    }.getOrNull()

    fun save(context: Context, profile: UserProfile) {
        val value = JSONObject().apply {
            put("uid", profile.uid)
            put("name", profile.name)
            put("email", profile.email)
            put("phone", profile.phone)
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, value.toString()).apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove(KEY).apply()
    }
}
