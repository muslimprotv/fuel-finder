# Fuel Finder v4 — Real In‑App Google Navigation

This v4 intentionally replaces the old animated route preview with **real Google turn-by-turn navigation inside Fuel Finder**.

## What is real now

- Real GPS position from the Android phone.
- Real nearby petrol stations from Places API (New), filtered with `gas_station`.
- Real Google route calculation to the selected station.
- Google navigation UI embedded in Fuel Finder through `SupportNavigationFragment`.
- Tilted road-following camera.
- Voice turn-by-turn guidance.
- Traffic-aware Google navigation and automatic rerouting handled by Navigation SDK.
- No Intent that opens the separate Google Maps app.
- No fake moving car and no route simulator in production code.
- Real PSO, Shell, TotalEnergies, Attock and GO brand artwork when names match.

## Google setup required

1. Create/select a Google Cloud project with billing enabled.
2. Enable **Navigation SDK for Android**.
3. Enable **Places API (New)**.
4. Create an Android API key and restrict it to:
   - Android package: `com.fuelfinder.app`
   - Your app signing SHA-1 fingerprint
   - APIs: Navigation SDK for Android and Places API (New)
5. Copy `secrets.properties.example` to `secrets.properties` and paste the key:

   `MAPS_API_KEY=YOUR_KEY`

## Android requirements

- Android Studio with API 36 installed.
- Android 7 / API 24 or newer.
- Physical Android phone strongly recommended for real road navigation.
- Google Play services enabled.
- Precise location turned on.
- Network connection.

## Run

Open this folder in Android Studio, let Gradle sync, connect your Android phone, and press Run.

The first screen gets your live location and lists nearby fuel stations. Tap **Go**. Fuel Finder opens its own `NavigationActivity` and the Google navigation engine starts guidance to that station.

## Important

Google requires Navigation Terms acceptance and legal/attribution notices in production releases. Add `NOTICE.txt` and `LICENSES.txt` from the Navigation SDK to an About/Legal screen before Play Store publishing.

Brand logos are third-party trademarks and remain the property of their respective owners. The included mappings are for identifying the station brand in the user-facing locator.
