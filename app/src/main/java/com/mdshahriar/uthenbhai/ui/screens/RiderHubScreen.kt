package com.mdshahriar.uthenbhai.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.mdshahriar.uthenbhai.data.RecentPlacesStore
import com.mdshahriar.uthenbhai.data.RideHistoryEntry
import com.mdshahriar.uthenbhai.data.SavedPlacesStore
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.UserProfile
import com.mdshahriar.uthenbhai.model.VehicleType
import com.mdshahriar.uthenbhai.ui.components.BrandLogo
import com.mdshahriar.uthenbhai.ui.components.CustomLocationPickerSheet
import com.mdshahriar.uthenbhai.ui.components.VehicleIcon
import com.mdshahriar.uthenbhai.ui.locale.AppLanguage
import com.mdshahriar.uthenbhai.ui.locale.LocalAppLanguage
import com.mdshahriar.uthenbhai.ui.locale.tr
import com.mdshahriar.uthenbhai.ui.theme.success
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class RiderTab(val label: String, val icon: ImageVector) {
    RIDE("Home", Icons.Default.Home),
    ACTIVITY("Activity", Icons.AutoMirrored.Filled.ReceiptLong),
    PAYMENT("Payment", Icons.Default.Payments),
    ACCOUNT("Account", Icons.Default.Person)
}

/**
 * Modern floating bottom navigation bar inspired by Uber / Pathao.
 * Features pill highlights, spring scaling, and sleek borders.
 */
@Composable
fun RiderNavigationBar(selected: RiderTab, onSelect: (RiderTab) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RiderTab.entries.forEach { tab ->
                val active = selected == tab
                val localizedLabel = when (tab) {
                    RiderTab.RIDE -> tr("Home", "হোম")
                    RiderTab.ACTIVITY -> tr("Activity", "রাইডসমূহ")
                    RiderTab.PAYMENT -> tr("Payment", "পেমেন্ট")
                    RiderTab.ACCOUNT -> tr("Account", "অ্যাকাউন্ট")
                }

                val pillBg by animateColorAsState(
                    targetValue = if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    animationSpec = spring(),
                    label = "tabPillBg"
                )
                val tabScale by animateFloatAsState(
                    targetValue = if (active) 1.02f else 1.0f,
                    animationSpec = spring(),
                    label = "tabScale"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .scale(tabScale)
                        .selectable(selected = active, role = Role.Tab) { onSelect(tab) }
                        .background(pillBg, RoundedCornerShape(16.dp))
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = localizedLabel,
                        fontSize = 11.sp,
                        fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RiderHubScreen(
    tab: RiderTab,
    user: UserProfile,
    history: List<RideHistoryEntry>,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onTabSelected: (RiderTab) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    currentLocation: LatLng? = null
) {
    val context = LocalContext.current
    var savedHome by remember(user.uid) { mutableStateOf(SavedPlacesStore.load(context, user.uid, "home")) }
    var savedWork by remember(user.uid) { mutableStateOf(SavedPlacesStore.load(context, user.uid, "work")) }
    var editingPlace by remember { mutableStateOf<String?>(null) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { RiderNavigationBar(tab, onTabSelected) }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 24.dp, bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header with Brand Badge
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandLogo(Modifier.size(34.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "UTHEN BHAI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.8.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = when (tab) {
                            RiderTab.ACTIVITY -> tr("Activity", "রাইডসমূহ")
                            RiderTab.PAYMENT -> tr("Payment", "পেমেন্ট")
                            RiderTab.ACCOUNT -> tr("Account", "অ্যাকাউন্ট")
                            RiderTab.RIDE -> tr("Ride", "রাইড")
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = when (tab) {
                            RiderTab.ACTIVITY -> tr("Your completed journeys, all in one place", "আপনার সম্পন্ন রাইডগুলো এক জায়গায়")
                            RiderTab.PAYMENT -> tr("A clear way to pay for every ride", "প্রতিটি রাইডে সহজ পেমেন্ট")
                            RiderTab.ACCOUNT -> tr("Your ride preferences and support", "রাইড সেটিংস ও সহায়তা")
                            RiderTab.RIDE -> tr("Where are you headed?", "কোথায় যাবেন?")
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when (tab) {
                RiderTab.ACTIVITY -> {
                    if (history.isEmpty()) {
                        item {
                            EmptyHubCard(
                                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                                title = tr("No rides yet", "এখনো কোনো রাইড নেই"),
                                body = tr("Once you finish a ride, its receipt will appear here.", "রাইড শেষ হলে রসিদ এখানে দেখা যাবে।"),
                                action = tr("Book your first ride", "প্রথম রাইড বুক করুন"),
                                onClick = { onTabSelected(RiderTab.RIDE) }
                            )
                        }
                    } else {
                        item {
                            val completed = history.filter { it.status == "completed" }
                            val total = completed.sumOf { it.fareTk + it.tipTk }
                            HubCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    HubIcon(Icons.Default.CheckCircle, MaterialTheme.colorScheme.success)
                                    Spacer(Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = tr("${completed.size} completed rides", "${completed.size}টি সম্পন্ন রাইড"),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = tr("৳$total total fare paid", "মোট ভাড়া ৳$total"),
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        items(history, key = { it.id }) { ride -> RideHistoryCard(ride) }
                    }
                }

                RiderTab.PAYMENT -> {
                    item {
                        HubCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                HubIcon(Icons.Default.Payments, MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = tr("Cash after your ride", "রাইড শেষে নগদ পরিশোধ"),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = tr("Pay your driver directly in taka", "চালককে সরাসরি টাকা দিন"),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = tr(
                                    "Your fare is shown before you book. You can review the breakdown and apply a promo code during checkout.",
                                    "বুকিংয়ের আগে ভাড়া দেখতে পারবেন। ভাড়ার হিসাব দেখুন এবং প্রোমো কোড ব্যবহার করুন।"
                                ),
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    item {
                        HubCard {
                            Text(
                                text = tr("How it works", "যেভাবে কাজ করে"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            InfoStep("1", tr("Choose your ride", "রাইড বেছে নিন"),
                                tr("Compare bike, CNG, and car fares.", "বাইক, সিএনজি ও গাড়ির ভাড়া তুলনা করুন।"))
                            InfoStep("2", tr("Confirm the fare", "ভাড়া নিশ্চিত করুন"),
                                tr("See the estimate and any discount before booking.", "বুকিংয়ের আগে আনুমানিক ভাড়া ও ছাড় দেখুন।"))
                            InfoStep("3", tr("Pay at drop-off", "গন্তব্যে পরিশোধ করুন"),
                                tr("Pay your driver directly and keep a receipt in Activity.", "চালককে সরাসরি টাকা দিন; রসিদ রাইডসমূহে থাকবে।"))
                        }
                    }
                    item {
                        HubCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                HubIcon(Icons.Default.LocalOffer, MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = tr("Have a promo code?", "প্রোমো কোড আছে?"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = tr("Enter UTHEN20 or FIRST50 on the fare preview for instant discounts.",
                                            "বুকিংয়ের আগে ভাড়ার পাতায় UTHEN20 বা FIRST50 কোড লিখুন।"),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                RiderTab.ACCOUNT -> {
                    // Profile Header Card
                    item {
                        HubCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.name.take(1).uppercase(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(user.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        user.email,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    // Saved Places (Home & Work)
                    item {
                        HubCard {
                            Text(
                                text = tr("Saved places", "সংরক্ষিত স্থান"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            SavedPlace(Icons.Default.Home, tr("Home", "বাড়ি"), savedHome?.address.orEmpty()) {
                                editingPlace = "home"
                            }
                            Spacer(Modifier.height(12.dp))
                            SavedPlace(Icons.Default.Work, tr("Work", "কর্মস্থল"), savedWork?.address.orEmpty()) {
                                editingPlace = "work"
                            }
                        }
                    }

                    // Theme Toggle
                    item {
                        HubCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                HubIcon(
                                    if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(tr("Dark theme", "ডার্ক থিম"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        tr("Use a more comfortable night view", "রাতে আরামদায়ক দৃশ্য ব্যবহার করুন"),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                val darkThemeLabel = tr("Dark theme", "ডার্ক থিম")
                                Switch(
                                    checked = isDarkTheme,
                                    onCheckedChange = onToggleTheme,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.semantics {
                                        contentDescription = darkThemeLabel
                                    }
                                )
                            }
                        }
                    }

                    // Language Selector
                    item {
                        HubCard(modifier = Modifier.clickable { showLanguageDialog = true }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                HubIcon(Icons.Default.Language, MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(tr("App language", "অ্যাপের ভাষা"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        if (appLanguage == AppLanguage.BANGLA) "বাংলা" else "English",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Help & Safety
                    item { HelpAndSafetyCard() }

                    // Sign Out
                    item {
                        HubCard(modifier = Modifier.clickable(onClick = onSignOut)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(14.dp))
                                Text(
                                    text = tr("Sign out", "সাইন আউট"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                RiderTab.RIDE -> Unit
            }
        }
    }

    editingPlace?.let { slot ->
        SavedPlacePickerDialog(
            userId = user.uid,
            currentLocation = currentLocation,
            title = if (slot == "home") tr("Save Home", "বাড়ির ঠিকানা রাখুন")
            else tr("Save Work", "কর্মস্থলের ঠিকানা রাখুন"),
            hasCurrentPlace = if (slot == "home") savedHome != null else savedWork != null,
            onSelect = { place ->
                SavedPlacesStore.save(context, user.uid, slot, place)
                if (slot == "home") savedHome = place else savedWork = place
                editingPlace = null
            },
            onDismiss = { editingPlace = null }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(tr("App language", "অ্যাপের ভাষা"), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf(AppLanguage.ENGLISH to "English", AppLanguage.BANGLA to "বাংলা")
                        .forEach { (language, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onLanguageChange(language)
                                        showLanguageDialog = false
                                    }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = if (appLanguage == language) FontWeight.Bold else FontWeight.Normal,
                                    color = if (appLanguage == language) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (appLanguage == language) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(tr("Close", "বন্ধ করুন"))
                }
            }
        )
    }
}

@Composable
private fun RideHistoryCard(ride: RideHistoryEntry) {
    var showDetails by remember { mutableStateOf(false) }
    val cancelled = ride.status == "cancelled"
    val dateLocale = if (LocalAppLanguage.current == AppLanguage.BANGLA)
        Locale.forLanguageTag("bn-BD") else Locale.getDefault()

    val vehicleType = when {
        ride.vehicle.contains("CNG", ignoreCase = true) -> VehicleType.CNG
        ride.vehicle.contains("Car", ignoreCase = true) -> VehicleType.CAR
        else -> VehicleType.BIKE
    }

    HubCard(modifier = Modifier.clickable { showDetails = true }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                VehicleIcon(
                    vehicle = vehicleType,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = ride.destination,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = SimpleDateFormat("d MMM yyyy • h:mm a", dateLocale).format(Date(ride.timestamp)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (cancelled) tr("Cancelled", "বাতিল") else "৳${ride.fareTk + ride.tipTk}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = if (cancelled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${ride.pickup}  →  ${ride.destination}",
                modifier = Modifier.weight(1f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = tr("View receipt", "রসিদ দেখুন"),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (showDetails) {
        AlertDialog(
            onDismissRequest = { showDetails = false },
            title = {
                Text(
                    text = if (cancelled) tr("Cancelled ride", "বাতিল রাইড") else tr("Ride receipt", "রাইডের রসিদ"),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tr("From: ${ride.pickup}", "শুরু: ${ride.pickup}"))
                    Text(tr("To: ${ride.destination}", "গন্তব্য: ${ride.destination}"))
                    Text(tr("Ride: ${ride.vehicle}", "রাইড: ${ride.vehicle}"))
                    if (cancelled) {
                        Text(tr("Reason: ${ride.cancelReason.orEmpty()}", "কারণ: ${ride.cancelReason.orEmpty()}"))
                        Text(tr("No charge", "কোনো খরচ নেই"), fontWeight = FontWeight.Bold)
                    } else {
                        Text(tr("Driver: ${ride.driver}", "চালক: ${ride.driver}"))
                        Text(tr("Fare: ৳${ride.fareTk}", "ভাড়া: ৳${ride.fareTk}"))
                        if (ride.tipTk > 0) Text(tr("Tip: ৳${ride.tipTk}", "বকশিশ: ৳${ride.tipTk}"))
                        Text(
                            text = tr("Paid in cash: ৳${ride.fareTk + ride.tipTk}", "নগদ পরিশোধ: ৳${ride.fareTk + ride.tipTk}"),
                            fontWeight = FontWeight.Bold
                        )
                        if (ride.rating > 0) {
                            Text(tr("Your rating: ${ride.rating} ★", "আপনার রেটিং: ${ride.rating} ★"))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetails = false }) {
                    Text(tr("Done", "শেষ"))
                }
            }
        )
    }
}

@Composable
private fun HelpAndSafetyCard() {
    var expanded by remember { mutableStateOf(false) }
    HubCard(modifier = Modifier.clickable { expanded = !expanded }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HubIcon(Icons.Default.Shield, MaterialTheme.colorScheme.success)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(tr("Help & safety", "সহায়তা ও নিরাপত্তা"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    tr("Ride tips and support information", "রাইডের পরামর্শ ও সহায়তা"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (expanded) {
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Spacer(Modifier.height(14.dp))
            Text(tr("Before you go", "যাত্রার আগে"), fontWeight = FontWeight.Bold)
            Text(
                tr(
                    "Check the driver's name and vehicle plate in the trip card before boarding. Share your journey details with someone you trust. In an emergency, contact local emergency services.",
                    "ওঠার আগে চালকের নাম ও গাড়ির নম্বর মিলিয়ে নিন। বিশ্বস্ত কাউকে রাইডের তথ্য জানান। জরুরি অবস্থায় স্থানীয় জরুরি সেবায় যোগাযোগ করুন।"
                ),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    tr("This portfolio app uses simulated driver matching.", "এই পোর্টফোলিও অ্যাপে চালক খোঁজা সিমুলেটেড।"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyHubCard(
    icon: ImageVector,
    title: String,
    body: String,
    action: String,
    onClick: () -> Unit
) {
    HubCard {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HubIcon(icon, MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(14.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(body, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Text(
                text = action,
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HubCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), content = content)
    }
}

@Composable
private fun HubIcon(icon: ImageVector, tint: Color) {
    Box(
        Modifier
            .size(42.dp)
            .background(tint.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun InfoStep(number: String, title: String, body: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .size(26.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                number,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(body, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SavedPlace(icon: ImageVector, label: String, address: String, onClick: () -> Unit) {
    val emptyAddress = tr("No address saved", "কোনো ঠিকানা সংরক্ষিত নেই")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HubIcon(icon, MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                address.ifBlank { emptyAddress },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = tr("Edit $label", "$label পরিবর্তন করুন"),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SavedPlacePickerDialog(
    userId: String,
    currentLocation: LatLng?,
    title: String,
    hasCurrentPlace: Boolean,
    onSelect: (AppLocation?) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    CustomLocationPickerSheet(
        title = title,
        userId = userId,
        currentLocation = currentLocation,
        homeAddress = SavedPlacesStore.load(context, userId, "home"),
        workAddress = SavedPlacesStore.load(context, userId, "work"),
        onSelectLocation = { onSelect(it) },
        onDismiss = onDismiss,
        onClearSavedPlace = if (hasCurrentPlace) ({ onSelect(null) }) else null
    )
}
