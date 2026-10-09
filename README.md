# 🚗 Uthen Bhai (উঠেন ভাই) - Modern Ride-Sharing Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0+)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

**Uthen Bhai** is a modern, high-performance ride-sharing Android application engineered with **Kotlin**, **Jetpack Compose (Material 3)**, and **Clean Architecture**. Designed around a realistic Dhaka commuter journey, it features an ultra-refined, stylish UI/UX inspired by **Pathao** and **Uber** with smooth spring micro-animations, custom vector vehicle illustrations, and rich bilingual (English / Bengali) support.

---

## 🌟 Modern UI & UX Highlights

The app moves completely away from default Jetpack Compose template patterns to deliver an intuitive, classy, and responsive experience:

### 🎨 1. Custom Vector Artwork & Vehicle Illustrations
- **Motorbike (Bike)**: Sporty commuter motorcycle featuring dual-tone alloy rims, brake calipers, metallic exhaust pipe, aerodynamic tank contours, and soft ground contact drop shadow.
- **CNG Auto-Rickshaw**: Iconic Dhaka-style 3-wheeler with deep green canopy, safety side rails, illuminated headlight beam, and bumper accents.
- **Sedan (Car)**: Classy modern sedan with aerodynamic body contours, tinted windshield reflection streak, headlamp/taillight details, alloy wheels, and ambient blur shadows.
- **Unified `VehicleIcon` Glyphs**: Custom silhouette vectors embedded seamlessly into buttons, chips, and ride selection sheets.

### 🔤 2. Premium Typography: Plus Jakarta Sans
- Replaced standard system fonts with **Plus Jakarta Sans**, a modern geometric sans-serif typeface designed for clean legibility and premium aesthetic balance across headlines, metrics, and bilingual text.

### ✨ 3. Fluid Motion & Micro-Animations
- **Sonar Radar Driver Matching**: Multi-ring animated sonar radar wave with smooth pulsing ripple effects and centered vehicle illumination.
- **Spring Scale Interactions**: Vehicle selection cards, navigation tabs, and actionable chips feature physical spring bounce dynamics on press and selection.
- **Interactive Cash Receipt**: Animated 5-star rating bar with playful micro-bounce transitions and real-time tipping calculations.

---

## 📱 End-to-End Rider Journey

| Stage | Features & Experience |
|---|---|
| **1. Onboarding & Access** | Location permission gate, GPS hardware check, Google Sign-In or Demo Profile creation, and guest mode. |
| **2. Home & Discovery** | "Where to today?" quick search, interactive vehicle category cards, Saved Places (**Home** & **Work**), and cached search history. |
| **3. Interactive Map & Planner** | Dual-engine map (Google Maps / OpenStreetMap), live GPS centering, stop swap shortcut, and connected route timeline (green pickup node ➔ vertical connector ➔ orange destination node). |
| **4. Fare Preview & Customization** | Dynamic pricing quote, route quality indicator (road route vs. offline fallback), promo code input with instant discount calculation, payment method selector, and passenger notes. |
| **5. Driver Matching** | Sonar ripple animation searching for nearby simulated drivers with cancellation dialog. |
| **6. Pickup Verification** | 4-digit security PIN verification card matching passenger and driver before trip inception. |
| **7. Live Trip Progress** | 4-step route stepper, live driver profile & license plate badge, safety toolkit, and emergency sharing. |
| **8. Cash Payment & Receipt** | Itemized trip bill breakdown, interactive tip increments (+৳20, +৳50, +৳100), driver rating, and trip history persistence. |

---

## 💰 Transparent Pricing Model

Uthen Bhai implements a transparent ride-sharing pricing model:

$$\text{Fare} = \max\left(\text{Minimum}, \text{Base} + (\text{Distance} \times \text{Rate}_{\text{km}}) + (\text{Time} \times \text{Rate}_{\text{min}})\right) + \text{BusyHour} - \text{Discount}$$

| Vehicle | Base Fare | Per Km | Per Min | Minimum Fare |
|:---|:---:|:---:|:---:|:---:|
| 🏍️ **Bike** | ৳30 | ৳18 | ৳1.20 | ৳60 |
| 🛺 **CNG** | ৳55 | ৳24 | ৳1.80 | ৳95 |
| 🚗 **Car** | ৳100 | ৳38 | ৳2.80 | ৳180 |

* **Busy-Hour Adjustment**: 12% adjustment automatically applied during Dhaka peak commuting hours (`08:00–09:59` and `17:00–19:59`).
* **Promo Codes**: Interactive coupon code redemption engine with validation.

---

## 🛠️ Architecture & Tech Stack

- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Language**: [Kotlin](https://kotlinlang.org/) (Coroutines, StateFlow, Serialization)
- **Architecture**: Clean Architecture & MVI/MVVM-inspired unidirectional state flow
- **Mapping & Geospatial**:
  - [Google Maps Compose SDK](https://github.com/googlemaps/android-maps-compose)
  - [Google Places API (New)](https://developers.google.com/maps/documentation/places/web-service)
  - [OpenStreetMap / osmdroid](https://github.com/osmdroid/osmdroid) (automatic offline-safe fallback)
  - [Geoapify Geocoding & Routing API](https://www.geoapify.com/)
- **Authentication & Backend**:
  - Firebase Authentication (Google Sign-In)
  - Cloud Firestore
  - Encrypted local persistence for demo sessions & ride history
- **Design & Styling**:
  - Plus Jakarta Sans typography
  - Dynamic Light & Dark Theme with full WCAG 4.5:1+ contrast compliance

---

## 📁 Project Structure

```
com.mdshahriar.uthenbhai/
├── data/
│   ├── DeviceLocationService.kt    # GPS & fused location provider
│   ├── FirebaseRepository.kt       # Firebase Auth & Firestore sync
│   ├── GeoapifyService.kt          # Fallback geocoding & road polyline
│   ├── PlaceSearchService.kt       # Google Places autocomplete & place details
│   ├── RideHistoryStore.kt         # Local completed/cancelled trip storage
│   ├── RideSessionStore.kt         # Trip lifecycle state persistence
│   └── SavedPlacesStore.kt         # Home, Work, and recent destination cache
├── model/
│   ├── LocationModels.kt           # AppLocation, LatLng representations
│   └── RideModels.kt               # VehicleType, ActiveRide, FareBreakdown, Driver
├── ui/
│   ├── components/
│   │   ├── ActiveTripSheet.kt            # Live journey progress & driver card
│   │   ├── BrandLogo.kt                  # App emblem & monogram
│   │   ├── CashPaymentReceiptDialog.kt   # Itemized cash bill, tips & star rating
│   │   ├── CustomLocationPickerSheet.kt  # Search suggestions & map picker
│   │   ├── DriverMatchingOverlay.kt      # Sonar radar matching animation
│   │   ├── InteractiveStreetMap.kt       # Map rendering & markers
│   │   ├── LocationAccessGate.kt         # GPS & permission onboarding
│   │   ├── RideBookingSheet.kt           # Vehicle selector & price breakdown
│   │   ├── RideFlowExtras.kt             # Pickup PIN & cancellation dialog
│   │   └── VehicleSticker.kt             # Custom vector artwork & VehicleIcon
│   ├── locale/
│   │   └── AppLanguage.kt          # English & Bengali localization engine
│   ├── screens/
│   │   ├── LoginScreen.kt          # Authentication & demo profile setup
│   │   ├── MainRideScreen.kt       # Central map, floating controls & orchestrator
│   │   ├── ProfileScreen.kt        # User settings, dark mode & safety
│   │   ├── RiderHomeScreen.kt      # Pathao/Uber-style main landing screen
│   │   └── RiderHubScreen.kt       # Navigation bar, Activity & Payment tabs
│   └── theme/
│       ├── Color.kt                # Vibrant brand palette & dark mode tokens
│       ├── Theme.kt                # High-contrast MaterialTheme provider
│       └── Type.kt                 # Plus Jakarta Sans typography scale
└── util/
    ├── FareCalculator.kt           # Deterministic pricing engine
    └── RouteHelper.kt              # Distance, polyline & speed calculations
```

---

## 🚀 Getting Started

### 1. Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17 or higher
- Android SDK API 34+ (Min SDK: 26)

### 2. Clone the Repository
```bash
git clone https://github.com/Shahriar811/UthenBhai.git
cd UthenBhai
```

### 3. Configure API Keys
The project uses a zero-leak secrets architecture. Sensitive API keys are never checked into version control.

1. Copy the example configuration file:
   ```bash
   cp local.properties.example local.properties
   ```
2. Open `local.properties` and add your keys:
   ```properties
   sdk.dir=YOUR_ANDROID_SDK_PATH
   MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
   GEOAPIFY_API_KEY=YOUR_OPTIONAL_GEOAPIFY_KEY
   ```
   > **Note**: If `MAPS_API_KEY` is not provided, the app will automatically fall back to OpenStreetMap tiles so all features remain functional out of the box!

### 4. Build and Run
```bash
# On Linux/macOS
./gradlew assembleDebug

# On Windows (PowerShell)
.\gradlew.bat assembleDebug
```

---

## 🧪 Testing & Verification

The project includes an extensive test suite verifying ride calculations, pricing formulas, and UI regressions:

```bash
# Run unit & logic tests
.\gradlew.bat testDebugUnitTest

# Run on connected device/emulator
.\gradlew.bat connectedDebugAndroidTest
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
