package com.mdshahriar.uthenbhai

import com.mdshahriar.uthenbhai.model.VehicleType
import com.mdshahriar.uthenbhai.util.FareCalculator
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FareCalculatorTest {
    private val dhaka = ZoneId.of("Asia/Dhaka")
    private fun at(hour: Int): Long = ZonedDateTime.of(2026, 10, 4, hour, 0, 0, 0, dhaka)
        .toInstant().toEpochMilli()

    @Test
    fun distanceDurationAndVehicleChangeTheQuote() {
        val bike = FareCalculator.calculateFare(6.4, 22, VehicleType.BIKE,
            quoteEpochMillis = at(14))
        val cng = FareCalculator.calculateFare(6.4, 22, VehicleType.CNG,
            quoteEpochMillis = at(14))
        val car = FareCalculator.calculateFare(6.4, 22, VehicleType.CAR,
            quoteEpochMillis = at(14))
        val slowerBike = FareCalculator.calculateFare(6.4, 32, VehicleType.BIKE,
            quoteEpochMillis = at(14))

        assertEquals(171, bike.totalFareTk)
        assertEquals(249, cng.totalFareTk)
        assertEquals(405, car.totalFareTk)
        assertTrue(slowerBike.totalFareTk > bike.totalFareTk)
    }

    @Test
    fun busyHourIsExplicitAndQuoteTimeIsDeterministic() {
        val normal = FareCalculator.calculateFare(6.4, 22, VehicleType.BIKE,
            quoteEpochMillis = at(14))
        val busy = FareCalculator.calculateFare(6.4, 22, VehicleType.BIKE,
            quoteEpochMillis = at(18))

        assertEquals(0, normal.busyHourAdjustmentTk)
        assertEquals(12, busy.busyHourPercent)
        assertEquals(21, busy.busyHourAdjustmentTk)
        assertEquals(192, busy.totalFareTk)
        assertEquals(busy, FareCalculator.calculateFare(6.4, 22, VehicleType.BIKE,
            quoteEpochMillis = at(18)))
    }

    @Test
    fun minimumAndPromoKeepBreakdownReconcilable() {
        val quote = FareCalculator.calculateFare(0.1, 1, VehicleType.CNG,
            discountTk = 1000, quoteEpochMillis = at(18))
        val sum = quote.baseFare + quote.distanceCost + quote.timeCost +
            quote.minimumFareAdjustmentTk + quote.busyHourAdjustmentTk - quote.discountTk

        assertTrue(quote.minimumFareAdjustmentTk > 0)
        assertEquals(sum, quote.totalFareTk)
        assertEquals(10, quote.totalFareTk)
    }

    @Test
    fun promoRecalculatesForEachRideAndBikeCodeDoesNotDiscountCar() {
        val bike = FareCalculator.calculateFareWithPromo(6.4, 22, VehicleType.BIKE,
            "UTHEN20", at(14))
        val car = FareCalculator.calculateFareWithPromo(6.4, 22, VehicleType.CAR,
            "UTHEN20", at(14))
        val bikeOnlyOnCar = FareCalculator.calculateFareWithPromo(6.4, 22, VehicleType.CAR,
            "FREEBIKE", at(14))

        assertEquals(34, bike.discountTk)
        assertEquals(50, car.discountTk)
        assertEquals(0, bikeOnlyOnCar.discountTk)
    }
}
