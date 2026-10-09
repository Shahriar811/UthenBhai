package com.mdshahriar.uthenbhai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.FareBreakdown
import com.mdshahriar.uthenbhai.model.RouteInfo
import com.mdshahriar.uthenbhai.model.VehicleType
import com.mdshahriar.uthenbhai.ui.locale.tr
import com.mdshahriar.uthenbhai.ui.theme.success
import com.mdshahriar.uthenbhai.util.FareCalculator
import com.mdshahriar.uthenbhai.util.RouteHelper

@Composable
fun RideBookingSheet(
    pickup: AppLocation,
    destination: AppLocation,
    routeInfo: RouteInfo,
    selectedVehicle: VehicleType,
    quoteEpochMillis: Long,
    onSelectVehicle: (VehicleType) -> Unit,
    fareBreakdown: FareBreakdown,
    routeNotice: String,
    routeLoading: Boolean,
    canRetryRoute: Boolean,
    onRetryRoute: () -> Unit,
    paymentMode: String,
    onSelectPaymentMode: (String) -> Unit,
    promoCode: String?,
    discountTk: Int,
    onApplyPromo: (String) -> Boolean,
    noteToDriver: String,
    onUpdateNote: (String) -> Unit,
    onChangePickup: () -> Unit,
    onChangeDestination: () -> Unit,
    onConfirmBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFareDetails by remember { mutableStateOf(false) }
    var showPromoInput by remember { mutableStateOf(false) }
    var enteredPromo by remember { mutableStateOf("") }
    var promoError by remember { mutableStateOf<String?>(null) }
    var promoSuccess by remember { mutableStateOf<String?>(null) }

    val vehicleOptions = remember {
        listOf(VehicleType.BIKE, VehicleType.CNG, VehicleType.CAR)
    }

    val paymentOptions = remember { listOf("Cash Direct") }

    val quickNotes = listOf(
        tr("At the main gate", "মূল ফটকে থাকব"),
        tr("Call on arrival", "এলে ফোন করুন"),
        tr("Have backpack/luggage", "সঙ্গে ব্যাগ আছে")
    ) + if (selectedVehicle == VehicleType.BIKE) listOf(tr("Need helmet", "হেলমেট লাগবে")) else emptyList()
    val promoSuccessMessage = tr("Voucher applied successfully!", "ভাউচার যোগ হয়েছে!")
    val promoErrorMessage = tr("Invalid voucher. Try 'UTHEN20' or 'FIRST50'",
        "ভাউচারটি সঠিক নয়। 'UTHEN20' বা 'FIRST50' চেষ্টা করুন।")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Drag handle pill
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = tr("Review your ride", "রাইডটি দেখে নিন"),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = tr("Check your route, ride and fare", "পথ, যানবাহন ও ভাড়া দেখে নিন"),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (routeNotice.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        routeNotice,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (canRetryRoute) {
                        Text(
                            tr("Retry road route", "সড়কপথ আবার খুঁজুন"),
                            modifier = Modifier
                                .clickable(onClick = onRetryRoute)
                                .heightIn(min = 48.dp)
                                .padding(top = 8.dp, bottom = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Route Points Visualizer (Pickup & Drop-off)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timeline nodes and connector
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(MaterialTheme.colorScheme.success, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        // Pickup Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChangePickup() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tr("PICKUP", "পিকআপ"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.success,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = pickup.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = tr("Edit pickup", "পিকআপ পরিবর্তন করুন"),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Destination Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChangeDestination() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tr("DESTINATION", "গন্তব্য"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = destination.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = tr("Edit destination", "গন্তব্য পরিবর্তন করুন"),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Route Meta Info Pill (Distance & Duration)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tr("Distance: ${routeInfo.distanceKm} km", "দূরত্ব: ${routeInfo.distanceKm} কিমি"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = tr("Est. Ride Time: ~${routeInfo.durationMinutes} mins", "আনুমানিক সময়: ~${routeInfo.durationMinutes} মিনিট"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle Category Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tr("Choose vehicle", "যানবাহন বেছে নিন"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (fareBreakdown.busyHourPercent > 0)
                        tr("Busy hours +${fareBreakdown.busyHourPercent}%", "ব্যস্ত সময় +${fareBreakdown.busyHourPercent}%")
                    else tr("Standard fare", "সাধারণ ভাড়া"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (fareBreakdown.busyHourPercent > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.success
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vehicle Options Cards (Bike, CNG, Car)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                vehicleOptions.forEach { vType ->
                    val isSelected = vType == selectedVehicle
                    val calculatedFare = FareCalculator.calculateFareWithPromo(
                        distanceKm = routeInfo.distanceKm,
                        durationMinutes = if (vType == selectedVehicle) routeInfo.durationMinutes
                        else RouteHelper.estimatedDurationMinutes(routeInfo.distanceKm, vType),
                        vehicleType = vType,
                        promoCode = promoCode,
                        quoteEpochMillis = quoteEpochMillis
                    )
                    val animatedFare by animateIntAsState(
                        targetValue = calculatedFare.totalFareTk,
                        label = "${vType.name} fare"
                    )

                    val animatedBorderColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        label = "borderColor"
                    )
                    val animatedBgColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface,
                        label = "bgColor"
                    )
                    val animatedScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.01f else 1.0f,
                        animationSpec = spring(),
                        label = "scale"
                    )
                    val animatedBorderWidth by animateDpAsState(
                        targetValue = if (isSelected) 2.dp else 1.dp,
                        label = "borderWidth"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(animatedScale)
                            .border(animatedBorderWidth, animatedBorderColor, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectVehicle(vType) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // High-def Vehicle Illustration
                            VehicleSticker(vType, Modifier.size(width = 68.dp, height = 48.dp))

                            Spacer(modifier = Modifier.width(10.dp))

                            // Vehicle Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = vType.title,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    // Badge Tag
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = vType.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = tr("${vType.capacity} • ${vType.etaMinutes} mins away",
                                            "${vType.capacity} জন • ${vType.etaMinutes} মিনিট দূরে"),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Price Column
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "৳ $animatedFare",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                if (discountTk > 0 && isSelected) {
                                    Text(
                                        text = "৳ ${calculatedFare.totalFareTk + calculatedFare.discountTk}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    )
                                } else {
                                    Text(
                                        text = tr("Cash Direct", "সরাসরি নগদ"),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.success,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Mode Selection Row
            Text(
                text = tr("Payment method", "পেমেন্ট পদ্ধতি"),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paymentOptions.forEach { method ->
                    val isSelected = paymentMode == method
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onSelectPaymentMode(method) }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (method == "Cash Direct") tr("Cash Direct", "সরাসরি নগদ") else method,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Promo Code Accordion / Voucher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showPromoInput = !showPromoInput }
                    .heightIn(min = 48.dp)
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (promoCode != null)
                            tr("Promo applied: $promoCode (-৳$discountTk)", "প্রোমো যুক্ত: $promoCode (-৳$discountTk)")
                        else tr("Have a promo code / voucher?", "প্রোমো কোড বা ভাউচার আছে?"),
                        fontSize = 12.sp,
                        fontWeight = if (promoCode != null) FontWeight.Bold else FontWeight.Medium,
                        color = if (promoCode != null) MaterialTheme.colorScheme.success else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Icon(
                    imageVector = if (showPromoInput) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = showPromoInput) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = enteredPromo,
                            onValueChange = {
                                enteredPromo = it
                                promoError = null
                                promoSuccess = null
                            },
                            placeholder = { Text("e.g. UTHEN20, FIRST50", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val success = onApplyPromo(enteredPromo)
                                if (success) {
                                    promoSuccess = promoSuccessMessage
                                    promoError = null
                                } else {
                                    promoError = promoErrorMessage
                                    promoSuccess = null
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(tr("Apply", "যোগ করুন"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (promoSuccess != null) {
                        Text(
                            text = promoSuccess!!,
                            color = MaterialTheme.colorScheme.success,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                    if (promoError != null) {
                        Text(
                            text = promoError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Note to Driver
            Text(
                text = tr("Note for driver (optional)", "চালকের জন্য বার্তা (ঐচ্ছিক)"),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickNotes.forEach { note ->
                    val isSelected = noteToDriver == note
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .clickable {
                                onUpdateNote(if (isSelected) "" else note)
                            }
                            .heightIn(min = 48.dp)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = note,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Detailed Fare Breakdown Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showFareDetails = !showFareDetails }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = tr("Fare breakdown", "ভাড়ার হিসাব"),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tr("Detailed fare breakdown", "বিস্তারিত ভাড়ার হিসাব"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (showFareDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = showFareDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp)
                ) {
                    FareRow(tr("Base fare (${selectedVehicle.title})", "মূল ভাড়া (${selectedVehicle.title})"), "৳ ${fareBreakdown.baseFare}")
                    FareRow(
                        tr("Distance (${fareBreakdown.distanceKm} km × ৳${selectedVehicle.perKmRate.toInt()}/km)",
                            "দূরত্ব (${fareBreakdown.distanceKm} কিমি × ৳${selectedVehicle.perKmRate.toInt()}/কিমি)"),
                        "৳ ${fareBreakdown.distanceCost}"
                    )
                    FareRow(
                        tr("Trip time (${fareBreakdown.durationMinutes} min × ৳${selectedVehicle.perMinuteRate}/min)",
                            "যাত্রার সময় (${fareBreakdown.durationMinutes} মিনিট × ৳${selectedVehicle.perMinuteRate}/মিনিট)"),
                        "৳ ${fareBreakdown.timeCost}"
                    )
                    if (fareBreakdown.minimumFareAdjustmentTk > 0) {
                        FareRow(tr("Minimum fare adjustment", "সর্বনিম্ন ভাড়া সমন্বয়"), "+৳ ${fareBreakdown.minimumFareAdjustmentTk}")
                    }
                    if (fareBreakdown.busyHourAdjustmentTk > 0) {
                        FareRow(tr("Busy hours (${fareBreakdown.busyHourPercent}%)",
                            "ব্যস্ত সময় (${fareBreakdown.busyHourPercent}%)"),
                            "+৳ ${fareBreakdown.busyHourAdjustmentTk}")
                    }
                    if (discountTk > 0) {
                        FareRow(tr("Promo discount", "প্রোমো ছাড়"), "-৳ ${fareBreakdown.discountTk}", isDiscount = true)
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    FareRow(tr("Estimated fare (${paymentMode})", "আনুমানিক ভাড়া"), "৳ ${fareBreakdown.totalFareTk}", isBold = true)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        tr("Based on this route, estimated time and ride type. A 12% busy-hour adjustment applies at 8–10 AM and 5–8 PM in Dhaka. This demo does not use live demand data.",
                            "পথ, আনুমানিক সময় ও যানবাহন অনুযায়ী ভাড়া। ঢাকায় সকাল ৮–১০টা ও বিকাল ৫–৮টায় ১২% ব্যস্ত সময়ের সমন্বয় যোগ হয়। এই ডেমোতে লাইভ চাহিদার তথ্য নেই।"),
                        fontSize = 11.sp, lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main CTA Action Button with Vehicle Icon Glyph
            Button(
                onClick = onConfirmBooking,
                enabled = !routeLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    VehicleIcon(
                        vehicle = selectedVehicle,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (routeLoading)
                            tr("Checking route and fare…", "পথ ও ভাড়া যাচাই হচ্ছে…")
                        else tr("Book ${selectedVehicle.title} • ৳${fareBreakdown.totalFareTk}",
                            "${selectedVehicle.title} বুক করুন • ৳${fareBreakdown.totalFareTk}"),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun FareRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isDiscount) MaterialTheme.colorScheme.success else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isDiscount) MaterialTheme.colorScheme.success else if (isBold) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}
