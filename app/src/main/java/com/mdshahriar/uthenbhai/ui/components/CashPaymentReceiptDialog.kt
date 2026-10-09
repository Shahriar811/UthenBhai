package com.mdshahriar.uthenbhai.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mdshahriar.uthenbhai.model.ActiveRide
import com.mdshahriar.uthenbhai.ui.locale.tr
import com.mdshahriar.uthenbhai.ui.theme.StarYellow
import com.mdshahriar.uthenbhai.ui.theme.success

@Composable
fun CashPaymentReceiptDialog(
    ride: ActiveRide,
    onPaymentConfirmed: (rating: Int, tipTk: Int) -> Unit
) {
    var selectedRating by remember { mutableIntStateOf(5) }
    var selectedTip by remember { mutableIntStateOf(0) }

    val tipOptions = listOf(0, 10, 20, 50)
    val totalWithTip = ride.fareBreakdown.totalFareTk + selectedTip

    Dialog(
        onDismissRequest = { /* Must explicitly confirm cash */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cash Payment Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.success.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = tr("Cash payment", "নগদ পেমেন্ট"),
                        tint = MaterialTheme.colorScheme.success,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = tr("Trip completed!", "রাইড সম্পন্ন!"),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = tr("Please pay the driver in cash", "চালককে নগদ টাকা দিন"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Big Total Bill Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tr("TOTAL AMOUNT PAYABLE", "মোট পরিশোধযোগ্য"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.success,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "৳ $totalWithTip",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tr(
                                "${ride.vehicleType.title} • ${ride.fareBreakdown.distanceKm} km with ${ride.driver.name}",
                                "${ride.vehicleType.title} • ${ride.fareBreakdown.distanceKm} কিমি • ${ride.driver.name}"
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown Items
                Column(modifier = Modifier.fillMaxWidth()) {
                    ReceiptRow(
                        label = tr("Base fare (${ride.vehicleType.title})", "মূল ভাড়া (${ride.vehicleType.title})"),
                        value = "৳ ${ride.fareBreakdown.baseFare}"
                    )
                    ReceiptRow(
                        label = tr("Distance fare (${ride.fareBreakdown.distanceKm} km)", "দূরত্বের ভাড়া (${ride.fareBreakdown.distanceKm} কিমি)"),
                        value = "৳ ${ride.fareBreakdown.distanceCost}"
                    )
                    ReceiptRow(
                        label = tr("Trip time (${ride.fareBreakdown.durationMinutes} min)", "যাত্রার সময় (${ride.fareBreakdown.durationMinutes} মিনিট)"),
                        value = "৳ ${ride.fareBreakdown.timeCost}"
                    )
                    if (ride.fareBreakdown.minimumFareAdjustmentTk > 0) {
                        ReceiptRow(
                            label = tr("Minimum fare adjustment", "সর্বনিম্ন ভাড়া সমন্বয়"),
                            value = "+৳ ${ride.fareBreakdown.minimumFareAdjustmentTk}"
                        )
                    }
                    if (ride.fareBreakdown.busyHourAdjustmentTk > 0) {
                        ReceiptRow(
                            label = tr("Busy hours (${ride.fareBreakdown.busyHourPercent}%)", "ব্যস্ত সময় (${ride.fareBreakdown.busyHourPercent}%)"),
                            value = "+৳ ${ride.fareBreakdown.busyHourAdjustmentTk}"
                        )
                    }
                    if (ride.fareBreakdown.discountTk > 0) {
                        ReceiptRow(
                            label = tr("Promo discount (${ride.promoCode ?: "Voucher"})", "প্রোমো ছাড় (${ride.promoCode ?: "ভাউচার"})"),
                            value = "-৳ ${ride.fareBreakdown.discountTk}",
                            isHighlight = true
                        )
                    }
                    if (selectedTip > 0) {
                        ReceiptRow(
                            label = tr("Driver tip (thank you!)", "চালকের বকশিশ"),
                            value = "+৳ $selectedTip",
                            isHighlight = true
                        )
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    ReceiptRow(
                        label = tr("Payment option", "পেমেন্ট পদ্ধতি"),
                        value = tr("Cash direct", "সরাসরি নগদ"),
                        isHighlight = false
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tip Rider Option
                Text(
                    text = tr("Add a tip for ${ride.driver.name}?", "${ride.driver.name}-কে বকশিশ দেবেন?"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tipOptions.forEach { tip ->
                        val isSelected = selectedTip == tip
                        val tipBg by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            label = "tipBg"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(tipBg)
                                .clickable { selectedTip = tip }
                                .heightIn(min = 44.dp)
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (tip == 0) tr("No tip", "বকশিশ নয়") else "+৳$tip",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rate Rider Stars
                Text(
                    text = tr("Rate your experience with ${ride.driver.name}", "${ride.driver.name}-এর রাইড রেটিং দিন"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        val isStarred = i <= selectedRating
                        val starScale by animateFloatAsState(
                            targetValue = if (isStarred) 1.15f else 1.0f,
                            animationSpec = spring(),
                            label = "starScale$i"
                        )
                        Icon(
                            imageVector = if (isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = tr("Rate $i stars", "$i তারকা রেটিং"),
                            tint = if (isStarred) StarYellow else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(44.dp)
                                .scale(starScale)
                                .clickable { selectedRating = i }
                                .padding(4.dp)
                        )
                    }
                }

                val feedbackReaction = when (selectedRating) {
                    5 -> tr("🌟 Excellent ride! Super smooth.", "🌟 দারুণ রাইড!")
                    4 -> tr("👍 Good experience.", "👍 ভালো অভিজ্ঞতা।")
                    3 -> tr("👌 Average trip.", "👌 মোটামুটি রাইড।")
                    2 -> tr("⚠️ Needs improvement.", "⚠️ আরও ভালো হওয়া দরকার।")
                    else -> tr("❌ Poor trip.", "❌ রাইড ভালো হয়নি।")
                }

                Text(
                    text = feedbackReaction,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Cash Paid Button
                Button(
                    onClick = { onPaymentConfirmed(selectedRating, selectedTip) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.success,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tr("I paid ৳$totalWithTip in cash", "আমি নগদ ৳$totalWithTip পরিশোধ করেছি"),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) MaterialTheme.colorScheme.success else MaterialTheme.colorScheme.onSurface
        )
    }
}
