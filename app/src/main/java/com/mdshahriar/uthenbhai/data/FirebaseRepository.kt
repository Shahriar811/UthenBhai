package com.mdshahriar.uthenbhai.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mdshahriar.uthenbhai.model.ActiveRide
import com.mdshahriar.uthenbhai.model.UserProfile
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val context: Context) {

    private val tag = "FirebaseRepository"
    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    init {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                auth = FirebaseAuth.getInstance()
                firestore = FirebaseFirestore.getInstance()
                Log.d(tag, "Firebase initialized successfully with project")
            } else {
                Log.w(tag, "FirebaseApp not initialized. Fallback mode enabled.")
            }
        } catch (e: Exception) {
            Log.w(tag, "Firebase initialization skipped: ${e.localizedMessage}")
        }
    }

    fun isFirebaseReady(): Boolean = auth != null && firestore != null

    fun getCurrentUser(): UserProfile {
        val fbUser = auth?.currentUser
        return if (fbUser != null) {
            UserProfile(
                uid = fbUser.uid,
                name = fbUser.displayName ?: "Uthen Bhai Rider",
                email = fbUser.email.orEmpty(),
                phone = fbUser.phoneNumber.orEmpty()
            )
        } else {
            // Guest profiles do not contain invented saved addresses or ride history.
            UserProfile(
                uid = "demo_user_bd",
                name = "Guest Rider"
            )
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Boolean {
        return try {
            firestore?.collection("users")
                ?.document(profile.uid)
                ?.set(profile, SetOptions.merge())
                ?.await()
            firestore != null
        } catch (e: Exception) {
            Log.e(tag, "Failed to save user profile: ${e.message}")
            false
        }
    }

    suspend fun saveRideRecord(ride: ActiveRide, tipTk: Int = 0): Boolean {
        return try {
            val record = hashMapOf(
                "rideId" to ride.id,
                "pickupName" to ride.pickup.name,
                "destinationName" to ride.destination.name,
                "vehicleType" to ride.vehicleType.name,
                "distanceKm" to ride.fareBreakdown.distanceKm,
                "durationMinutes" to ride.fareBreakdown.durationMinutes,
                "baseFareTk" to ride.fareBreakdown.baseFare,
                "distanceFareTk" to ride.fareBreakdown.distanceCost,
                "timeFareTk" to ride.fareBreakdown.timeCost,
                "minimumFareAdjustmentTk" to ride.fareBreakdown.minimumFareAdjustmentTk,
                "busyHourAdjustmentTk" to ride.fareBreakdown.busyHourAdjustmentTk,
                "discountTk" to ride.fareBreakdown.discountTk,
                "fareTk" to ride.fareBreakdown.totalFareTk,
                "tipTk" to tipTk,
                "totalFareTk" to ride.fareBreakdown.totalFareTk + tipTk,
                "paymentMethod" to ride.paymentMode,
                "driverName" to ride.driver.name,
                "bikeModel" to ride.driver.bikeModel,
                "timestamp" to System.currentTimeMillis()
            )

            firestore?.collection("rides")
                ?.document(ride.id)
                ?.set(record)
                ?.await()
            firestore != null
        } catch (e: Exception) {
            Log.e(tag, "Failed to save ride record: ${e.message}")
            false
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e(tag, "Sign out error: ${e.message}")
        }
    }
}
