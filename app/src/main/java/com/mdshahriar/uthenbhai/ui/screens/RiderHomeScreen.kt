package com.mdshahriar.uthenbhai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdshahriar.uthenbhai.data.RecentPlacesStore
import com.mdshahriar.uthenbhai.data.RideHistoryEntry
import com.mdshahriar.uthenbhai.data.SavedPlacesStore
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.model.UserProfile
import com.mdshahriar.uthenbhai.model.VehicleType
import com.mdshahriar.uthenbhai.ui.components.BrandLogo
import com.mdshahriar.uthenbhai.ui.components.VehicleSticker
import com.mdshahriar.uthenbhai.ui.locale.tr
import com.mdshahriar.uthenbhai.ui.theme.success

@Composable
fun RiderHomeScreen(
    user: UserProfile,
    history: List<RideHistoryEntry>,
    currentLocationLabel: String?,
    isDarkTheme: Boolean,
    onChooseVehicle: (VehicleType) -> Unit,
    onSearchDestination: () -> Unit,
    onChoosePickup: () -> Unit,
    onChooseSavedPlace: (AppLocation) -> Unit,
    onTabSelected: (RiderTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val home = remember(user.uid) { SavedPlacesStore.load(context, user.uid, "home") }
    val work = remember(user.uid) { SavedPlacesStore.load(context, user.uid, "work") }
    val recentPlaces = remember(user.uid) { RecentPlacesStore.load(context, user.uid) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { RiderNavigationBar(RiderTab.RIDE, onTabSelected) }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header: Brand & Profile Avatar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrandLogo(
                        Modifier
                            .size(46.dp)
                            .shadow(4.dp, RoundedCornerShape(14.dp))
                    )
                    Column(
                        Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = "UTHEN BHAI",
                            fontSize = 13.sp,
                            letterSpacing = 1.6.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = tr("Move your way • Dhaka", "আপনার মতো চলুন • ঢাকা"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Account pill button
                    Surface(
                        modifier = Modifier
                            .size(46.dp)
                            .clickable(role = Role.Button) { onTabSelected(RiderTab.ACCOUNT) },
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape,
                        shadowElevation = 2.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.name.take(1).uppercase(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                        }
                    }
                }
            }

            // Hero "Where to today?" Search Section (Uber / Pathao Style)
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(180))
                ) {
                    Column {
                        Text(
                            text = tr("Hello, ${user.name.substringBefore(' ')} 👋", "স্বাগতম, ${user.name.substringBefore(' ')} 👋"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = tr("Where to today?", "আজ কোথায় যাবেন?"),
                            fontSize = 30.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(Modifier.height(16.dp))

                        // Pathao / Uber Style Elevated Search Trigger Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button, onClick = onSearchDestination),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Search Icon with modern saffron background glow
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column(
                                    Modifier
                                        .weight(1f)
                                        .padding(start = 14.dp)
                                ) {
                                    Text(
                                        text = tr("Search destination", "গন্তব্য খুঁজুন"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = tr("Address, place or landmark", "ঠিকানা, স্থান বা ল্যান্ডমার্ক"),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Pickup Location Row Pill
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(role = Role.Button, onClick = onChoosePickup),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(MaterialTheme.colorScheme.success, CircleShape)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = tr("Pickup: ", "পিকআপ: "),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentLocationLabel ?: tr("Choose pickup location", "পিকআপ স্থান বেছে নিন"),
                                    fontSize = 12.sp,
                                    color = if (currentLocationLabel != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // "Choose your ride" Vehicle Selector Carousel
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = tr("Choose your ride", "রাইড বেছে নিন"),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = tr("Instant pickup across Dhaka city", "ঢাকা শহরজুড়ে দ্রুত পিকআপ"),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(VehicleType.BIKE, VehicleType.CNG, VehicleType.CAR).forEach { type ->
                            VehicleHomeCard(
                                type = type,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onChooseVehicle(type) }
                            )
                        }
                    }
                }
            }

            // Trust & Upfront Fare Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF2C241E) else Color(0xFFFFF4EB)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(
                                text = tr("Ride with confidence", "নিশ্চিন্তে যাত্রা করুন"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = tr("Upfront fare guarantee • No hidden charges", "বুকিংয়ের আগেই নির্দিষ্ট ভাড়া • কোনো গোপন খরচ নেই"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Saved Places (Home & Work)
            item {
                Column {
                    Text(
                        text = tr("Your places", "আপনার স্থান"),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(10.dp))
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.animateContentSize()) {
                            HomePlaceRow(
                                icon = Icons.Default.Home,
                                title = tr("Home", "বাড়ি"),
                                place = home?.address,
                                iconBg = MaterialTheme.colorScheme.primaryContainer,
                                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                                onClick = {
                                    if (home != null) onChooseSavedPlace(home)
                                    else onTabSelected(RiderTab.ACCOUNT)
                                }
                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 68.dp, end = 16.dp)
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            )
                            HomePlaceRow(
                                icon = Icons.Default.Work,
                                title = tr("Work", "কর্মস্থল"),
                                place = work?.address,
                                iconBg = MaterialTheme.colorScheme.success.copy(alpha = 0.14f),
                                iconTint = MaterialTheme.colorScheme.success,
                                onClick = {
                                    if (work != null) onChooseSavedPlace(work)
                                    else onTabSelected(RiderTab.ACCOUNT)
                                }
                            )
                        }
                    }
                }
            }

            // Recent Searches
            if (recentPlaces.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = tr("Recent searches", "সাম্প্রতিক খোঁজ"),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(10.dp))
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column {
                                recentPlaces.forEachIndexed { index, place ->
                                    if (index > 0) {
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(start = 68.dp, end = 16.dp)
                                                .height(1.dp)
                                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                        )
                                    }
                                    HomePlaceRow(
                                        icon = Icons.Default.History,
                                        title = place.name,
                                        place = place.address,
                                        iconBg = MaterialTheme.colorScheme.surfaceVariant,
                                        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        onClick = { onChooseSavedPlace(place) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Ride Shortcut
            if (history.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTabSelected(RiderTab.ACTIVITY) },
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = tr("Your recent ride", "সাম্প্রতিক রাইড"),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = history.first().destination,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = tr("View activity", "রাইড দেখুন"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleHomeCard(
    type: VehicleType,
    isDarkTheme: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(),
        label = "vehicleCardScale"
    )

    val background = if (isDarkTheme) MaterialTheme.colorScheme.surface else when (type) {
        VehicleType.BIKE -> Color(0xFFF3F7FD)
        VehicleType.CNG -> Color(0xFFF0F9F3)
        VehicleType.CAR -> Color(0xFFFFF6F0)
    }

    val badgeColor = when (type) {
        VehicleType.BIKE -> Color(0xFF3B82F6)
        VehicleType.CNG -> Color(0xFF10B981)
        VehicleType.CAR -> Color(0xFFF97316)
    }

    Card(
        modifier = modifier
            .scale(cardScale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = if (isDarkTheme) 0.35f else 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High-def Vehicle Illustration
            VehicleSticker(type, Modifier.fillMaxWidth().height(60.dp))

            Spacer(Modifier.height(8.dp))

            Text(
                text = when (type) {
                    VehicleType.BIKE -> tr("Bike", "বাইক")
                    VehicleType.CNG -> tr("CNG", "সিএনজি")
                    VehicleType.CAR -> tr("Car", "গাড়ি")
                },
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Micro category pill
            Box(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .background(badgeColor.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = when (type) {
                        VehicleType.BIKE -> tr("Fastest", "দ্রুততম")
                        VehicleType.CNG -> tr("Everyday", "প্রতিদিন")
                        VehicleType.CAR -> tr("Comfort", "আরামদায়ক")
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
            }

            Spacer(Modifier.height(8.dp))

            // Action arrow
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
private fun HomePlaceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    place: String?,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(
            Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = place ?: tr("Add an address", "ঠিকানা যোগ করুন"),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}
