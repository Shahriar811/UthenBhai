package com.mdshahriar.uthenbhai

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.maps.model.LatLng
import com.mdshahriar.uthenbhai.data.RecentPlacesStore
import com.mdshahriar.uthenbhai.data.RideSession
import com.mdshahriar.uthenbhai.data.RideSessionStore
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.RideState
import com.mdshahriar.uthenbhai.model.UserProfile
import com.mdshahriar.uthenbhai.model.VehicleType
import com.mdshahriar.uthenbhai.ui.components.CustomLocationPickerSheet
import com.mdshahriar.uthenbhai.ui.components.LocationPermissionCard
import com.mdshahriar.uthenbhai.ui.locale.AppLanguage
import com.mdshahriar.uthenbhai.ui.screens.LoginScreen
import com.mdshahriar.uthenbhai.ui.screens.RiderHomeScreen
import com.mdshahriar.uthenbhai.ui.screens.RiderHubScreen
import com.mdshahriar.uthenbhai.ui.screens.RiderTab
import com.mdshahriar.uthenbhai.ui.theme.UthenBhaiTheme
import com.mdshahriar.uthenbhai.ui.theme.success
import java.io.File
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiRegressionTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val profile = "ui_audit_${System.nanoTime()}"

    private fun place(index: Int) = AppLocation("search_$index", "Search $index",
        "Address $index", LatLng(23.7 + index * 0.001, 90.4))

    @After fun removeTestProfile() {
        listOf("recent_place_searches", "ride_sessions").forEach {
            context.getSharedPreferences(it, Context.MODE_PRIVATE).edit().remove(profile).commit()
        }
    }

    @Test fun permissionPromptExplainsPreciseForegroundAccess() {
        var requests = 0
        compose.setContent { UthenBhaiTheme(false) {
            LocationPermissionCard(onRequestPermission = { requests++ })
        } }
        compose.onNodeWithText("Allow precise location").assertIsDisplayed()
        compose.onNodeWithText("Allow location access").performClick()
        compose.runOnIdle { assertEquals(1, requests) }
        screenshot("permission")
    }

    @Test fun disabledGpsHasAnActionablePrompt() {
        var requests = 0
        compose.setContent { UthenBhaiTheme(true) {
            LocationPermissionCard(onRequestPermission = { requests++ }, needsGps = true)
        } }
        compose.onNodeWithText("Turn on GPS").assertIsDisplayed()
        compose.onNodeWithText("Enable GPS").performClick()
        compose.runOnIdle { assertEquals(1, requests) }
        screenshot("gps-dark")
    }

    @Test fun historyCapsDeduplicatesReordersAndPersistsPerProfile() {
        (1..6).forEach { RecentPlacesStore.record(context, profile, place(it)) }
        assertEquals(listOf(6, 5, 4, 3, 2).map { "search_$it" },
            RecentPlacesStore.load(context, profile).map { it.id })
        RecentPlacesStore.record(context, profile, place(3).copy(id = "new_id", name = "Updated address"))
        val reloaded = RecentPlacesStore.load(context.applicationContext, profile)
        assertEquals(5, reloaded.size)
        assertEquals("new_id", reloaded.first().id)
        assertFalse(reloaded.any { it.id == "search_3" })
        assertTrue(RecentPlacesStore.load(context, "${profile}_other").isEmpty())
        RecentPlacesStore.record(context, profile, place(9).copy(id = "saved_home"))
        assertEquals(reloaded, RecentPlacesStore.load(context, profile))
    }

    @Test fun startupDropsDraftLocationsButKeepsABookedJourney() {
        val draft = RideSession(place(1), place(2), VehicleType.CAR, RideState.ROUTE_PREVIEW,
            "draft", 4.0, 12, System.currentTimeMillis(), null, 0, "")
        RideSessionStore.save(context, profile, draft)
        assertNull(RideSessionStore.load(context, profile))
        val booked = draft.copy(state = RideState.TRIP_IN_PROGRESS, rideId = "booked",
            routePoints = listOf(place(1).latLng, place(2).latLng),
            driverPosition = place(1).latLng, nextRoutePoint = 1)
        RideSessionStore.save(context, profile, booked)
        assertEquals(booked, RideSessionStore.load(context, profile))
    }

    @Test fun homeStartsWithAnEmptyPickupAndNoPresetPlaces() {
        compose.setContent { UthenBhaiTheme(false) {
            RiderHomeScreen(UserProfile(uid = profile, name = "Demo Rider"), emptyList(), null,
                false, {}, {}, {}, {}, {})
        } }
        compose.onNodeWithText("Choose pickup location").assertIsDisplayed()
        compose.onNodeWithText("Popular destinations").assertDoesNotExist()
        compose.onNodeWithText("Gulshan 1 Circle").assertDoesNotExist()
        screenshot("home-light")
    }

    @Test fun locationPickerOnlyOffersHomeWorkAndFiveRecentsWhenEmpty() {
        (1..6).forEach { RecentPlacesStore.record(context, profile, place(it)) }
        var selected: AppLocation? = null
        val home = place(20).copy(id = "saved_home", name = "Home")
        compose.setContent { UthenBhaiTheme(false) {
            CustomLocationPickerSheet("Choose destination", profile,
                onSelectLocation = { selected = it }, onDismiss = {}, homeAddress = home)
        } }
        compose.onNodeWithText("Home").assertIsDisplayed()
        compose.onNodeWithText("Work").assertIsDisplayed()
        compose.onNodeWithText("Saved and popular places").assertDoesNotExist()
        compose.onNodeWithText("Use current location").assertDoesNotExist()
        compose.onNodeWithText("Search 1").assertDoesNotExist()
        compose.runOnIdle { assertNull(selected) }
        compose.onNodeWithText("Home").performClick()
        compose.runOnIdle { assertEquals(home, selected) }
    }

    @Test fun loginSupportsLargeTextInASmallWindow() {
        var loggedIn = false
        compose.setContent { UthenBhaiTheme(false) {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                Box(Modifier.size(280.dp, 420.dp)) {
                    LoginScreen(onLoginSuccess = { loggedIn = true })
                }
            }
        } }
        compose.onNodeWithText("Create a demo profile").performScrollTo().performClick()
        compose.onNodeWithText("Your name").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Explore as guest").performScrollTo().performClick()
        compose.runOnIdle { assertTrue(loggedIn) }
    }

    @Test fun darkAccountRemainsReadable() {
        compose.setContent { UthenBhaiTheme(true) {
            val colors = MaterialTheme.colorScheme
            assertTrue(contrast(colors.primary, colors.surface) >= 4.5f)
            assertTrue(contrast(colors.onPrimary, colors.primary) >= 4.5f)
            assertTrue(contrast(colors.onPrimaryContainer, colors.primaryContainer) >= 4.5f)
            assertTrue(contrast(colors.success, colors.surface) >= 4.5f)
            RiderHubScreen(RiderTab.ACCOUNT, UserProfile(uid = profile, name = "Demo Rider"),
                emptyList(), true, {}, AppLanguage.ENGLISH, {}, {}, {})
        } }
        compose.onAllNodesWithText("Home")[0].assertIsDisplayed()
        compose.onNodeWithText("Work").assertIsDisplayed()
        screenshot("account-dark")
    }

    private fun contrast(first: Color, second: Color): Float {
        val a = first.luminance()
        val b = second.luminance()
        return (maxOf(a, b) + 0.05f) / (minOf(a, b) + 0.05f)
    }

    private fun screenshot(name: String) {
        val output = File(context.getExternalFilesDir(null), "ui-audit").apply { mkdirs() }
        File(output, "$name.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
