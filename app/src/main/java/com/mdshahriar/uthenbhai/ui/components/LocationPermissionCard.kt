package com.mdshahriar.uthenbhai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mdshahriar.uthenbhai.ui.locale.tr

@Composable
fun LocationPermissionCard(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
    needsGps: Boolean = false,
    isLoading: Boolean = false,
    onOpenSettings: (() -> Unit)? = null
) {
    LocationStatusScreen(
        title = if (needsGps) tr("Turn on GPS", "জিপিএস চালু করুন")
            else tr("Allow precise location", "সঠিক অবস্থানের অনুমতি দিন"),
        message = if (needsGps) tr("Turn on your phone's location service to use Uthen Bhai. We use GPS to find your position on the map.",
            "উঠেন ভাই ব্যবহার করতে ফোনের লোকেশন সেবা চালু করুন। মানচিত্রে আপনার অবস্থান পেতে জিপিএস প্রয়োজন।")
            else tr("Choose Precise and While using the app in the permission dialog. Your pickup and destination stay empty until you choose them.",
                "অনুমতির ডায়ালগে সঠিক অবস্থান ও অ্যাপ ব্যবহারের সময় বেছে নিন। আপনি বেছে না নেওয়া পর্যন্ত পিকআপ ও গন্তব্য ফাঁকা থাকবে।"),
        actionLabel = if (needsGps) tr("Enable GPS", "জিপিএস চালু করুন")
            else tr("Allow location access", "অবস্থানের অনুমতি দিন"),
        onAction = onRequestPermission,
        isLoading = isLoading,
        onOpenSettings = onOpenSettings,
        modifier = modifier
    )
}

@Composable
fun LocationStatusScreen(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onOpenSettings: (() -> Unit)? = null
) {
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .safeDrawingPadding().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            BrandLogo(Modifier.size(64.dp))
            Spacer(Modifier.height(28.dp))
            Box(Modifier.size(84.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center) {
                if (isLoading) CircularProgressIndicator(Modifier.size(34.dp))
                else Icon(Icons.Default.MyLocation, null, tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(28.dp))
            Button(onClick = onAction, enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp)) {
                Text(actionLabel, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            }
            onOpenSettings?.let {
                TextButton(onClick = it) { Text(tr("Open app settings", "অ্যাপের সেটিংস খুলুন")) }
            }
        }
    }
}
