package com.mdshahriar.uthenbhai.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdshahriar.uthenbhai.model.VehicleType
import com.mdshahriar.uthenbhai.ui.locale.tr
import com.mdshahriar.uthenbhai.ui.theme.success
import kotlinx.coroutines.delay

@Composable
fun DriverMatchingOverlay(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    vehicleType: VehicleType = VehicleType.BIKE
) {
    val transition = rememberInfiniteTransition(label = "radar")

    val scale1 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale1"
    )

    val alpha1 by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val scale2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale2"
    )

    val alpha2 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    var searchStep by remember { mutableIntStateOf(0) }
    val searchSteps = listOf(
        tr("Preparing your ${vehicleType.title} demo ride...",
            "আপনার ${vehicleType.title} ডেমো রাইড প্রস্তুত হচ্ছে..."),
        tr("Connecting with nearby available drivers...",
            "নিকটস্থ চালকদের সঙ্গে সংযোগ করা হচ্ছে..."),
        tr("Finalizing your driver match...", "আপনার চালক নির্বাচন সম্পন্ন হচ্ছে...")
    )

    LaunchedEffect(Unit) {
        delay(900)
        searchStep = 1
        delay(1100)
        searchStep = 2
    }

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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Drag handle pill
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pulsing Sonar / Radar Rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .scale(scale2)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha2), CircleShape)
                )

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .scale(scale1)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha1), CircleShape)
                )

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(8.dp, CircleShape)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    VehicleSticker(vehicleType, Modifier.size(width = 76.dp, height = 54.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = tr("Connecting with ${vehicleType.title}...",
                    "${vehicleType.title} চালকের সঙ্গে সংযোগ হচ্ছে..."),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = searchSteps.getOrElse(searchStep) { searchSteps.first() },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.heightIn(min = 36.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(5.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Safety Guarantee Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.success.copy(alpha = 0.12f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.success,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tr("Verified drivers • Fast dispatch", "যাচাইকৃত চালক • দ্রুত যাত্রা"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.success
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tr("Cancel request", "অনুরোধ বাতিল করুন"),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
