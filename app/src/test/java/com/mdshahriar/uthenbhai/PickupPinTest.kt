package com.mdshahriar.uthenbhai

import com.mdshahriar.uthenbhai.ui.components.pickupPinForRide
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PickupPinTest {
    @Test
    fun pinIsStableAndFourDigitsForRestoredRide() {
        val rideId = "ride_1720000000000"
        val first = pickupPinForRide(rideId)

        assertEquals(first, pickupPinForRide(rideId))
        assertEquals(4, first.length)
        assertTrue(first.all(Char::isDigit))
    }
}
