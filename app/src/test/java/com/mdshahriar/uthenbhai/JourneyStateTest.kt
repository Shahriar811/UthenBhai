package com.mdshahriar.uthenbhai

import com.mdshahriar.uthenbhai.model.RideState
import com.mdshahriar.uthenbhai.util.shouldRestoreJourney
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JourneyStateTest {
    @Test fun unbookedDraftsNeverRestoreLocations() {
        listOf(RideState.IDLE, RideState.ROUTE_PREVIEW, RideState.PICKUP_DROP_SELECTION,
            RideState.PROFILE_VIEW, RideState.PERMISSION_REQUIRED).forEach {
            assertFalse(shouldRestoreJourney(it, true, true, true))
        }
    }
    @Test fun bookedJourneysRemainRecoverable() {
        listOf(RideState.SEARCHING_DRIVER, RideState.MATCH_UNAVAILABLE, RideState.DRIVER_ON_WAY,
            RideState.DRIVER_AT_PICKUP, RideState.TRIP_IN_PROGRESS, RideState.PAYMENT_DUE).forEach {
            assertTrue(shouldRestoreJourney(it, true, true, true))
        }
    }
    @Test fun incompleteBookingsCannotRestoreMarkers() {
        assertFalse(shouldRestoreJourney(RideState.DRIVER_ON_WAY, false, true, true))
        assertFalse(shouldRestoreJourney(RideState.DRIVER_ON_WAY, true, false, true))
        assertFalse(shouldRestoreJourney(RideState.DRIVER_ON_WAY, true, true, false))
    }
}
