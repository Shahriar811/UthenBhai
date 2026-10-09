package com.mdshahriar.uthenbhai.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdshahriar.uthenbhai.model.ActiveRide
import com.mdshahriar.uthenbhai.model.RideState
import com.mdshahriar.uthenbhai.model.VehicleType
import com.mdshahriar.uthenbhai.ui.locale.tr
import com.mdshahriar.uthenbhai.ui.theme.StarYellow
import com.mdshahriar.uthenbhai.ui.theme.success

@Composable
fun ActiveTripSheet(
    ride: ActiveRide,
    onCompleteTrip: () -> Unit,
    onCancelRide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isEnRoute = ride.state == RideState.DRIVER_ON_WAY
    val shareText = tr(
        "Uthen Bhai trip.\nDriver: ${ride.driver.name} (${ride.driver.plateNumber})\nVehicle: ${ride.vehicleType.title}\nHeading to: ${ride.destination.name}",
        "উঠেন ভাই রাইড।\nচালক: ${ride.driver.name} (${ride.driver.plateNumber})\nযানবাহন: ${ride.vehicleType.title}\nগন্তব্য: ${ride.destination.name}"
    )
    val shareTitle = tr("Share ride details", "রাইডের তথ্য শেয়ার করুন")

    var showSafetyDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }

    // Pulsing indicator animation
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Trip Status Header with Live Pulsing Badge
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEnRoute) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.success.copy(alpha = 0.14f)
                ),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .scale(pulseScale)
                                .size(28.dp)
                                .background(
                                    if (isEnRoute) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.success,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isEnRoute) {
                                VehicleIcon(
                                    vehicle = ride.vehicleType,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = if (isEnRoute)
                                    tr("Driver arriving (~${ride.vehicleType.etaMinutes} mins)",
                                        "চালক আসছেন (~${ride.vehicleType.etaMinutes} মিনিট)")
                                else tr("Trip in progress to ${ride.destination.name}",
                                    "${ride.destination.name} পথে যাত্রা চলছে"),
                                color = if (isEnRoute) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.success,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isEnRoute)
                                    tr("Meeting at pickup point", "পিকআপ স্থানে দেখা হবে")
                                else tr("Heading to destination", "গন্তব্যের দিকে যাওয়া হচ্ছে"),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "৳ ${ride.fareBreakdown.totalFareTk}",
                        color = if (isEnRoute) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.success,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trip Stepper Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TripStepItem(label = tr("Booked", "বুকড"), isCompleted = true, isActive = false)
                TripStepDivider(isCompleted = true)
                TripStepItem(label = tr("Arriving", "আসছেন"), isCompleted = !isEnRoute, isActive = isEnRoute)
                TripStepDivider(isCompleted = !isEnRoute)
                TripStepItem(label = tr("On trip", "যাত্রায়"), isCompleted = false, isActive = !isEnRoute)
                TripStepDivider(isCompleted = false)
                TripStepItem(label = tr("Drop-off", "গন্তব্য"), isCompleted = false, isActive = false)
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Driver Information Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val initials = ride.driver.name.split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString("")

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials.ifEmpty { "DR" },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ride.driver.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = ride.vehicleType.title,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${ride.driver.rating}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tr("(${ride.driver.completedRides} trips)", "(${ride.driver.completedRides}টি রাইড)"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Call Action Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.success)
                        .clickable { showContactDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = tr("Contact driver", "চালকের সঙ্গে যোগাযোগ"),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vehicle Model & Dhaka Registration Plate Pill
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = ride.driver.vehicleModel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = tr("Payment: ${ride.paymentMode} • Direct cash", "পেমেন্ট: নগদ • সরাসরি টাকা"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // License Plate Badge
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = ride.driver.plateNumber,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Actions Bar: Message, Share Trip, Safety
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showChatDialog = true },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(tr("Chat", "চ্যাট"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, shareTitle))
                    },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(tr("Share", "শেয়ার"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { showSafetyDialog = true },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(tr("Safety", "নিরাপত্তা"), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Complete Trip Action Button
            Button(
                onClick = onCompleteTrip,
                enabled = !isEnRoute,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEnRoute) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.success,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEnRoute) tr("Driver approaching pickup", "চালক পিকআপে আসছেন")
                    else tr("Arrived at destination • Pay cash", "গন্তব্যে পৌঁছেছেন • নগদ দিন"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (isEnRoute) {
                TextButton(onClick = onCancelRide, modifier = Modifier.fillMaxWidth()) {
                    Text(tr("Cancel this ride", "রাইড বাতিল করুন"), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showSafetyDialog = false },
            title = { Text(tr("Safety & emergency support", "নিরাপত্তা ও জরুরি সহায়তা"), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(tr("This portfolio trip is simulated and is not monitored by a dispatch team.",
                        "এই ডেমো রাইড সিমুলেটেড; কোনো দল এটি পর্যবেক্ষণ করছে না।"), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tr("For a real emergency, contact your local emergency service directly.",
                        "বাস্তব জরুরি অবস্থায় স্থানীয় জরুরি সেবায় সরাসরি যোগাযোগ করুন।"), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = { TextButton(onClick = { showSafetyDialog = false }) { Text(tr("Close", "বন্ধ করুন")) } }
        )
    }

    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text(tr("Contact driver", "চালকের সঙ্গে যোগাযোগ"), fontWeight = FontWeight.Bold) },
            text = { Text(tr("Driver calling is unavailable in this simulated trip.", "সিমুলেটেড রাইডে চালককে ফোন করা যায় না।")) },
            confirmButton = { TextButton(onClick = { showContactDialog = false }) { Text(tr("Got it", "বুঝেছি")) } }
        )
    }

    if (showChatDialog) {
        AlertDialog(
            onDismissRequest = { showChatDialog = false },
            title = { Text(tr("Message driver", "চালককে বার্তা"), fontWeight = FontWeight.Bold) },
            text = { Text(tr("Messaging is unavailable in this simulated trip.", "সিমুলেটেড রাইডে বার্তা পাঠানো যায় না।")) },
            confirmButton = { TextButton(onClick = { showChatDialog = false }) { Text(tr("Got it", "বুঝেছি")) } }
        )
    }
}

@Composable
private fun TripStepItem(label: String, isCompleted: Boolean, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(
                    color = if (isCompleted) MaterialTheme.colorScheme.success else if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(11.dp))
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else if (isCompleted) MaterialTheme.colorScheme.success else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TripStepDivider(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .width(28.dp)
            .height(2.5.dp)
            .background(
                if (isCompleted) MaterialTheme.colorScheme.success else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
    )
}
