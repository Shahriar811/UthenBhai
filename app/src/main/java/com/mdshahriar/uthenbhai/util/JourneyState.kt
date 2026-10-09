package com.mdshahriar.uthenbhai.util

import com.mdshahriar.uthenbhai.model.RideState

val RideState.isActiveJourney: Boolean
    get() = this in setOf(RideState.SEARCHING_DRIVER, RideState.MATCH_UNAVAILABLE,
        RideState.DRIVER_ON_WAY, RideState.DRIVER_AT_PICKUP,
        RideState.TRIP_IN_PROGRESS, RideState.PAYMENT_DUE)

fun shouldRestoreJourney(state: RideState, hasPickup: Boolean, hasDestination: Boolean,
    hasRideId: Boolean): Boolean = state.isActiveJourney && hasPickup && hasDestination && hasRideId
