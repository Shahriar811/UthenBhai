package com.mdshahriar.uthenbhai.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mdshahriar.uthenbhai.data.FirebaseRepository
import com.mdshahriar.uthenbhai.model.UserProfile
import com.mdshahriar.uthenbhai.ui.components.BrandLogo
import com.mdshahriar.uthenbhai.ui.locale.tr

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun LoginScreen(
    onLoginSuccess: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val firebaseRepo = remember { FirebaseRepository(context) }

    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var showEmailForm by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val googleFailedMessage = tr("Google sign-in couldn't finish. You can explore as a guest.",
        "গুগল সাইন ইন সম্পন্ন হয়নি। অতিথি হিসেবে দেখতে পারেন।")
    val googleCancelledMessage = tr("Google sign-in was cancelled.", "গুগল সাইন ইন বাতিল হয়েছে।")
    val googleUnavailableMessage = tr("Google sign-in is unavailable on this device.",
        "এই ডিভাইসে গুগল সাইন ইন পাওয়া যাচ্ছে না।")
    val invalidProfileMessage = tr("Enter your name and a valid email to continue.",
        "চালিয়ে যেতে নাম ও সঠিক ইমেইল লিখুন।")

    // Google Sign In Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                if (account != null) {
                    val profile = UserProfile(
                        uid = account.id ?: "google_${System.currentTimeMillis()}",
                        name = account.displayName ?: "Google User",
                        email = account.email.orEmpty(),
                        phone = ""
                    )
                    onLoginSuccess(profile)
                }
            } catch (e: Exception) {
                errorMessage = googleFailedMessage
            }
        } else {
            errorMessage = googleCancelledMessage
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .imePadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Emblem
            BrandLogo(
                Modifier
                    .size(92.dp)
                    .shadow(8.dp, RoundedCornerShape(22.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Uthen Bhai",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
            )

            Text(
                text = tr("Rides around Dhaka • Cash payment", "ঢাকায় রাইড • নগদ পেমেন্ট"),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Value Props row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeaturePill(text = tr("Upfront fares", "আগেই ভাড়া দেখুন"))
                FeaturePill(text = tr("Cash only", "শুধু নগদ"))
                FeaturePill(text = tr("Route preview", "পথ দেখুন"))
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tr("Choose how to continue", "চালিয়ে যাওয়ার উপায় বেছে নিন"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = tr("Explore the rider journey. Your demo history stays on this device.",
                            "রাইড অভিজ্ঞতা দেখুন। ডেমো রাইডের ইতিহাস এই ডিভাইসে থাকবে।"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Google Login Button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .clickable {
                                try {
                                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestEmail()
                                        .requestProfile()
                                        .build()
                                    val client = GoogleSignIn.getClient(context, gso)
                                    googleSignInLauncher.launch(client.signInIntent)
                                } catch (e: Exception) {
                                    errorMessage = googleUnavailableMessage
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            GoogleGIcon(modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = tr("Continue with Google", "গুগল দিয়ে চালিয়ে যান"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Divider with "OR"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        )
                        Text(
                            text = tr("  OR  ", "  অথবা  "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email / Demo Profile Option
                    if (!showEmailForm) {
                        OutlinedButton(
                            onClick = { showEmailForm = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tr("Create a demo profile", "ডেমো প্রোফাইল তৈরি করুন"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        // Email Form Fields
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(tr("Your name", "আপনার নাম")) },
                            placeholder = { Text("e.g. Shahriar Ahmed") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(tr("Email (for your demo profile)", "ইমেইল (ডেমো প্রোফাইলের জন্য)")) },
                            placeholder = { Text("e.g. yourname@gmail.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val finalName = nameInput.trim()
                                val finalEmail = emailInput.trim().lowercase()
                                if (finalName.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(finalEmail).matches()) {
                                    errorMessage = invalidProfileMessage
                                } else {
                                    onLoginSuccess(UserProfile(
                                        uid = "demo_${finalEmail.hashCode()}",
                                        name = finalName,
                                        email = finalEmail,
                                        phone = ""
                                    ))
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = tr("Continue to app", "অ্যাপে যান"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Guest / Skip Option
                    TextButton(
                        onClick = {
                            val guestProfile = UserProfile(
                                uid = "guest_demo",
                                name = "Guest Rider",
                                email = "Guest profile",
                                phone = ""
                            )
                            onLoginSuccess(guestProfile)
                        }
                    ) {
                        Text(
                            text = tr("Explore as guest", "অতিথি হিসেবে দেখুন"),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = tr("Portfolio demo • Driver matching and trips are simulated. Cash payment is shown for demonstration.",
                    "পোর্টফোলিও ডেমো • চালক খোঁজা ও যাত্রা সিমুলেটেড। নগদ পেমেন্ট শুধু দেখানোর জন্য।"),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun FeaturePill(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun GoogleGIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val size = size.minDimension
        val stroke = size * 0.22f
        val center = androidx.compose.ui.geometry.Offset(size / 2, size / 2)
        val radius = (size - stroke) / 2

        // Blue right & bar
        drawArc(
            color = Color(0xFF4285F4),
            topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Green bottom
        drawArc(
            color = Color(0xFF34A853),
            topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            startAngle = 45f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Yellow bottom-left
        drawArc(
            color = Color(0xFFFBBC05),
            topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Red top
        drawArc(
            color = Color(0xFFEA4335),
            topLeft = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Horizontal bar
        drawLine(
            color = Color(0xFF4285F4),
            start = center,
            end = androidx.compose.ui.geometry.Offset(size - stroke / 2, center.y),
            strokeWidth = stroke
        )
    }
}
