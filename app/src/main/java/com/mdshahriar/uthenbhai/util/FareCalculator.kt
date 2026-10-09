package com.mdshahriar.uthenbhai.util

import com.mdshahriar.uthenbhai.model.FareBreakdown
import com.mdshahriar.uthenbhai.model.VehicleType
import java.time.Instant
import java.time.ZoneId
import kotlin.math.max
import kotlin.math.roundToInt

object FareCalculator {
    private val pricingZone = ZoneId.of("Asia/Dhaka")
    private const val BUSY_HOUR_PERCENT = 12

    /** Transparent demo quote. Busy hours are a schedule, not live demand data. */
    fun calculateFare(
        distanceKm: Double,
        durationMinutes: Int,
        vehicleType: VehicleType = VehicleType.BIKE,
        discountTk: Int = 0,
        quoteEpochMillis: Long = System.currentTimeMillis()
    ): FareBreakdown {
        val safeDistance = if (distanceKm.isFinite()) max(0.1, distanceKm) else 0.1
        val billableDistance = (safeDistance * 10).roundToInt() / 10.0
        val billableMinutes = durationMinutes.coerceAtLeast(1)
        val distanceCost = (billableDistance * vehicleType.perKmRate).roundToInt()
        val timeCost = (billableMinutes * vehicleType.perMinuteRate).roundToInt()
        val subtotal = vehicleType.baseFare + distanceCost + timeCost
        val minimumAdjustment = max(0, vehicleType.minFare - subtotal)
        val adjustedSubtotal = subtotal + minimumAdjustment
        val localHour = Instant.ofEpochMilli(quoteEpochMillis).atZone(pricingZone).hour
        val busyHour = localHour in 8..9 || localHour in 17..19
        val busyPercent = if (busyHour) BUSY_HOUR_PERCENT else 0
        val busyAdjustment = (adjustedSubtotal * busyPercent / 100.0).roundToInt()
        val beforeDiscount = adjustedSubtotal + busyAdjustment
        val appliedDiscount = discountTk.coerceIn(0, beforeDiscount - 10)

        return FareBreakdown(
            distanceKm = billableDistance,
            durationMinutes = billableMinutes,
            vehicleType = vehicleType,
            baseFare = vehicleType.baseFare,
            distanceCost = distanceCost,
            timeCost = timeCost,
            minimumFareAdjustmentTk = minimumAdjustment,
            busyHourAdjustmentTk = busyAdjustment,
            busyHourPercent = busyPercent,
            discountTk = appliedDiscount,
            totalFareTk = beforeDiscount - appliedDiscount
        )
    }

    fun calculateAllFares(
        distanceKm: Double,
        durationMinutes: Int,
        discountTk: Int = 0,
        quoteEpochMillis: Long = System.currentTimeMillis()
    ): Map<VehicleType, FareBreakdown> {
        return VehicleType.values().associateWith { vType ->
            calculateFare(distanceKm, durationMinutes, vType, discountTk, quoteEpochMillis)
        }
    }

    fun calculateFareWithPromo(
        distanceKm: Double,
        durationMinutes: Int,
        vehicleType: VehicleType,
        promoCode: String?,
        quoteEpochMillis: Long = System.currentTimeMillis()
    ): FareBreakdown {
        val standard = calculateFare(distanceKm, durationMinutes, vehicleType,
            quoteEpochMillis = quoteEpochMillis)
        val discount = promoCode?.let { getDiscountForPromo(it, standard.totalFareTk, vehicleType) } ?: 0
        return if (discount > 0) calculateFare(distanceKm, durationMinutes, vehicleType,
            discount, quoteEpochMillis) else standard
    }

    fun getDiscountForPromo(code: String, fareTotal: Int,
                            vehicleType: VehicleType = VehicleType.BIKE): Int {
        val normalized = code.trim().uppercase()
        return when (normalized) {
            "UTHEN20" -> minOf(50, (fareTotal * 0.20).roundToInt())
            "FIRST50" -> minOf(50, fareTotal - 10)
            "DHAKA10" -> minOf(30, (fareTotal * 0.10).roundToInt())
            "FREEBIKE" -> if (vehicleType == VehicleType.BIKE) minOf(35, fareTotal - 10) else 0
            else -> 0
        }
    }
}
