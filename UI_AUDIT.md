# UI and location audit

Audit date: 5 October 2026.

## Implemented fixes

| Area | Result |
| --- | --- |
| Startup access | Precise foreground location permission and enabled GPS are required before login or rider screens. Permission denial, settings access, and GPS enablement have actionable screens. Access is rechecked on resume and provider changes. |
| Initial map | No preset viewport, pickup, or destination. A fresh device fix supplies the viewport and GPS indicator. No cached last-location substitution. GPS failure leaves stops empty and exposes retry/search actions. |
| Restored state | Unbooked drafts do not restore pins. Booked active journeys retain the quote, route geometry, driver position, and next simulated route point. |
| Search | Home, Work, and five recent unique selected searches are the empty-query options. Pickup and saved-address editors use the same modal search sheet. Proximity biases use the actual device position when available. |
| Map interaction | Modal search blocks underlying map taps. Map callbacks track the latest pickup/destination mode. Stale route requests cannot replace newer choices. Google marker state is stable; the alternate map no longer recenters on every driver update. |
| Navigation | Home navigation and system Back return from account/activity/payment and the editable ride planner correctly. Profiles have separate Compose state. |
| Appearance | Light/dark theme colors replace fixed light-only colors. Status/navigation icon appearance follows the theme. Vehicle stickers preserve proportions, Activity uses the correct vehicle icon, and launcher icons use the supplied logo with a themed monochrome variant. Unused Android template/sample icons and map screens were removed. |
| Motion | Short screen fades and ride-sheet transitions replace abrupt screen changes. Form expansion animates its size. Vehicle-selection and matching animations are retained. |
| Small screens and accessibility | Login supports scrolling, keyboard insets, and wrapping feature labels. Booking and active-trip sheets are height-bounded and scrollable. Search headings, account text, and cancellation choices can wrap. Main controls have appropriate minimum touch heights. |

GPS access handling follows Android's [runtime location permission guidance](https://developer.android.com/develop/sensors-and-location/location/permissions/runtime) and [location settings flow](https://developer.android.com/develop/sensors-and-location/location/change-location-settings).

## Verification

- Debug app build, nine JVM unit tests, Android lint, and Android screen-test package compilation passed during the audit. Final validation uses `:app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleDebugAndroidTest`.
- Eight instrumented regressions cover permission/GPS prompt actions, recent-search persistence and limits, active-session persistence, empty startup pickup, picker defaults, large-text login, and dark theme contrast. They use temporary local profiles.
- Device execution was attempted but could not start: Gradle reported **No connected devices**, and ADB showed no device. Screen tests and captured-screen visual inspection therefore remain unverified. Reconnect an unlocked USB-debugging device and run `:app:connectedDebugAndroidTest`.
- Lint's remaining advisory findings concern dependency versions and style suggestions. They are not suppressed with a baseline.

## Practical limits

This remains a portfolio simulation. Dispatch, driver motion, PIN verification, and cash payment are demo flows. Live map/search behavior depends on network access, configured API services, and device GPS reception. The fixed Dhaka coordinate remaining in the rider screen defines the demo service boundary only; it is not a map startup position or selected stop.
