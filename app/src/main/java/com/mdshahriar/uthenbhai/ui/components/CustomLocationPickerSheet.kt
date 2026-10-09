package com.mdshahriar.uthenbhai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.mdshahriar.uthenbhai.data.PlaceSearchService
import com.mdshahriar.uthenbhai.data.PlaceSuggestion
import com.mdshahriar.uthenbhai.data.RecentPlacesStore
import com.mdshahriar.uthenbhai.model.AppLocation
import com.mdshahriar.uthenbhai.ui.locale.tr
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CustomLocationPickerSheet(
    title: String,
    userId: String,
    onSelectLocation: (AppLocation) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    currentLocation: LatLng? = null,
    homeAddress: AppLocation? = null,
    workAddress: AppLocation? = null,
    onEditSavedPlaces: (() -> Unit)? = null,
    onPickFromMap: (() -> Unit)? = null,
    onClearSavedPlace: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }
    val recentPlaces = remember(userId) { RecentPlacesStore.load(context, userId) }
    var query by remember { mutableStateOf("") }
    var searchedQuery by remember { mutableStateOf("") }
    var onlinePlaces by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var onlineUnavailable by remember { mutableStateOf(false) }
    var resolvingId by remember { mutableStateOf<String?>(null) }
    var selectionError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LaunchedEffect(query, currentLocation) {
        val term = query.trim()
        onlinePlaces = emptyList()
        searchedQuery = ""
        onlineUnavailable = false
        selectionError = false
        loading = term.length >= 3
        if (term.length < 3) return@LaunchedEffect
        delay(350)
        val result = PlaceSearchService.search(context, term, currentLocation, sessionToken)
        onlinePlaces = result.suggestions
        onlineUnavailable = result.onlineUnavailable
        searchedQuery = term
        loading = false
    }

    val term = query.trim()
    val localPlaces = remember(term, homeAddress, workAddress, recentPlaces) {
        (listOfNotNull(homeAddress, workAddress) + recentPlaces)
            .filter { term.isEmpty() || it.name.contains(term, true) || it.address.contains(term, true) }
            .map { PlaceSuggestion(it.id, it.name, it.address, location = it) }
    }
    val suggestions = if (term.length >= 3 && searchedQuery == term) {
        (onlinePlaces + localPlaces).distinctBy { it.placeId ?: it.id }
    } else if (term.isBlank()) emptyList() else localPlaces

    fun selectSearchPlace(place: AppLocation) {
        RecentPlacesStore.record(context, userId, place)
        onSelectLocation(place)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .imePadding()
                .padding(top = 14.dp, start = 18.dp, end = 18.dp, bottom = 14.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = tr("Close", "বন্ধ করুন"),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                enabled = resolvingId == null,
                placeholder = {
                    Text(
                        tr("Search for area, road or landmark…", "এলাকা, সড়ক বা পরিচিত স্থান খুঁজুন…"),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = if (query.isNotEmpty()) {{
                    IconButton(onClick = { query = "" }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = tr("Clear search", "খোঁজ মুছুন"),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }} else null,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            if (loading) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        tr("Finding places…", "স্থান খোঁজা হচ্ছে…"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (selectionError) {
                Text(
                    tr("Could not open that place. Try another result or choose it on the map.",
                        "স্থানটি খোলা যায়নি। অন্য ফলাফল বা মানচিত্র ব্যবহার করুন।"),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (term.isBlank()) {
                    item {
                        SuggestionRow(
                            title = tr("Home", "বাড়ি"),
                            subtitle = homeAddress?.address ?: if (onEditSavedPlaces != null)
                                tr("Add home address", "বাড়ির ঠিকানা যোগ করুন") else tr("Not saved", "সংরক্ষিত নেই"),
                            icon = { Icon(Icons.Default.Home, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                            onClick = {
                                if (homeAddress != null) onSelectLocation(homeAddress) else onEditSavedPlaces?.invoke()
                            },
                            enabled = homeAddress != null || onEditSavedPlaces != null
                        )
                    }
                    item {
                        SuggestionRow(
                            title = tr("Work", "কর্মস্থল"),
                            subtitle = workAddress?.address ?: if (onEditSavedPlaces != null)
                                tr("Add work address", "কর্মস্থলের ঠিকানা যোগ করুন") else tr("Not saved", "সংরক্ষিত নেই"),
                            icon = { Icon(Icons.Default.Work, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                            onClick = {
                                if (workAddress != null) onSelectLocation(workAddress) else onEditSavedPlaces?.invoke()
                            },
                            enabled = workAddress != null || onEditSavedPlaces != null
                        )
                    }
                    item {
                        Text(
                            text = tr("Recent searches", "সাম্প্রতিক খোঁজ"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
                        )
                    }
                    if (recentPlaces.isEmpty()) {
                        item {
                            SearchMessage(tr("Your last 5 searched places will appear here.",
                                "আপনার খোঁজা শেষ ৫টি স্থান এখানে দেখা যাবে।"))
                        }
                    }
                    items(recentPlaces, key = { "recent_${it.id}" }) { place ->
                        SuggestionRow(
                            title = place.name,
                            subtitle = place.address,
                            icon = { Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                            onClick = { selectSearchPlace(place) }
                        )
                    }
                }
                if (term.isNotBlank() && term.length < 3 && suggestions.isEmpty()) {
                    item {
                        SearchMessage(tr("Type at least 3 characters to search places.",
                            "স্থান খুঁজতে অন্তত ৩টি অক্ষর লিখুন।"))
                    }
                }
                if (term.length >= 3 && !loading && suggestions.isEmpty()) {
                    item {
                        SearchMessage(if (onlineUnavailable)
                            tr("Online search is unavailable. Try another name or choose on the map.",
                                "অনলাইন খোঁজ পাওয়া যাচ্ছে না। অন্য নাম বা মানচিত্র ব্যবহার করুন।")
                        else tr("No matching places. Try a nearby area or landmark.",
                            "কোনো স্থান পাওয়া যায়নি। কাছের এলাকা বা পরিচিত স্থান লিখুন।"))
                    }
                }
                items(suggestions, key = { it.id }) { suggestion ->
                    SuggestionRow(
                        title = suggestion.title,
                        subtitle = suggestion.subtitle,
                        icon = {
                            if (resolvingId == suggestion.id) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                            else Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        },
                        onClick = {
                            if (resolvingId != null) return@SuggestionRow
                            selectionError = false
                            suggestion.location?.let { selectSearchPlace(it); return@SuggestionRow }
                            resolvingId = suggestion.id
                            scope.launch {
                                val selected = PlaceSearchService.resolve(context, suggestion, sessionToken)
                                resolvingId = null
                                if (selected != null) selectSearchPlace(selected) else selectionError = true
                            }
                        }
                    )
                }
                if (suggestions.any { it.isGoogleResult }) {
                    item {
                        Text(
                            text = "Google Maps",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp)
                        )
                    }
                }
            }

            onPickFromMap?.let { chooseOnMap ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = chooseOnMap)
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = tr("Choose on map", "মানচিত্রে বেছে নিন"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            onClearSavedPlace?.let { clearAddress ->
                TextButton(onClick = clearAddress, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = tr("Remove saved address", "সংরক্ষিত ঠিকানা মুছুন"),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
}

@Composable
private fun SearchMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    )
}
