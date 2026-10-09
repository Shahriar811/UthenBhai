package com.mdshahriar.uthenbhai.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdshahriar.uthenbhai.model.ActiveRide
import com.mdshahriar.uthenbhai.ui.locale.tr

fun pickupPinForRide(rideId: String): String =
    (1000 + ((rideId.hashCode().toLong() and 0x7fffffff) % 9000)).toString()

@Composable
fun MatchUnavailableSheet(reason: String, onRetry: () -> Unit, onEditRide: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = tr("Ride request unavailable", "রাইড অনুরোধ করা যাচ্ছে না"),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = reason,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = tr("Try again", "আবার চেষ্টা করুন"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            OutlinedButton(
                onClick = onEditRide,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = tr("Edit trip", "রাইড পরিবর্তন করুন"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun PickupVerificationSheet(
    ride: ActiveRide,
    onVerified: () -> Unit,
    onCancel: () -> Unit
) {
    val pin = remember(ride.id) { pickupPinForRide(ride.id) }
    var enteredPin by remember(ride.id) { mutableStateOf("") }
    var incorrect by remember(ride.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = tr("Your driver has arrived", "আপনার চালক এসে গেছেন"),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${ride.driver.name} • ${ride.driver.plateNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = tr("Check the driver and plate before boarding.",
                    "ওঠার আগে চালকের নাম ও গাড়ির নম্বর মিলিয়ে নিন।"),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Dotted PIN Verification Display Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tr("Pickup PIN: $pin", "পিকআপ পিন: $pin"),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = tr("This is a demo. Enter the PIN to verify pickup and start the trip.",
                            "এটি একটি ডেমো। পিন লিখে পিকআপ যাচাই করে যাত্রা শুরু করুন।"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            OutlinedTextField(
                value = enteredPin,
                onValueChange = {
                    enteredPin = it.filter(Char::isDigit).take(4)
                    incorrect = false
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(tr("Enter pickup PIN", "পিকআপ পিন লিখুন")) },
                placeholder = { Text("e.g. $pin") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                isError = incorrect,
                shape = RoundedCornerShape(14.dp)
            )

            if (incorrect) {
                Text(
                    text = tr("That PIN doesn't match. Try again.", "পিনটি মেলেনি। আবার চেষ্টা করুন।"),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    if (enteredPin == pin) onVerified() else incorrect = true
                },
                enabled = enteredPin.length == 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = tr("Verify & start trip", "যাচাই করে যাত্রা শুরু করুন"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = tr("Cancel this ride", "রাইড বাতিল করুন"),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun CancelRideDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    val reasons = listOf(
        tr("Plans changed", "পরিকল্পনা বদলেছে"),
        tr("Pickup location issue", "পিকআপ স্থানে সমস্যা"),
        tr("Driver issue", "চালক সংক্রান্ত সমস্যা"),
        tr("Other", "অন্যান্য")
    )
    var selected by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = tr("Cancel ride request?", "রাইড অনুরোধ বাতিল করবেন?"),
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = tr("Select a reason. This demo has no cancellation charge.",
                        "একটি কারণ বেছে নিন। এই ডেমোতে বাতিলের কোনো খরচ নেই।"),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clickable { selected = reason }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == reason,
                            onClick = { selected = reason },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = reason,
                            fontSize = 14.sp,
                            fontWeight = if (selected == reason) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selected?.let(onConfirm) },
                enabled = selected != null
            ) {
                Text(
                    text = tr("Cancel ride", "রাইড বাতিল করুন"),
                    fontWeight = FontWeight.Bold,
                    color = if (selected != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(tr("Keep ride", "রাইড চালু রাখুন"), fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
