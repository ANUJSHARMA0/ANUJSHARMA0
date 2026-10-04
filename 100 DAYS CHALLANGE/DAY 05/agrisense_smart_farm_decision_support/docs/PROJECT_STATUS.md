# AgriSense Project Status

## Currently implemented

- Native Kotlin/Jetpack Compose app with splash, onboarding, farm setup, dashboard, soil, weather, crop recommendations/details/comparison, What-If, scenario analysis, long-term planning, markets, sensors, alerts, profile, and farm report screens.
- Local demo data and separated domain calculations for recommendations, economics, scenario outcomes, and crop planning.
- Farm setup supports farmer name, location, area, soil type, irrigation, water availability, previous crop, goal, and planning season.
- What-If controls recalculate estimated yield, profit, and risk. Farm history is reachable from Profile and presents previous crop, yield, profit, and notes.
- Stitch HTML/screenshot references are organized in `docs/design-references/`; the design tokens and visual guidance are in `docs/design-system/DESIGN.md`.

## Still incomplete / future integration

- Weather, soil, market, and sensor readings are illustrative local demo data, not live services. No API, GPS, cloud persistence, authentication, ESP32, or Bluetooth integration is connected.
- Crop scores, yields, confidence, costs, risk, and plans are simplified models, not guarantees or professional agronomic advice.
- Language switching and automated end-to-end Android UI tests are not implemented.

## Verification

- `:app:testDebugUnitTest`: passed (10 tests).
- `:app:assembleDebug`: passed.
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk` (16,211,987 bytes).
- No Android device or emulator was connected for installation or runtime UI testing.
- Build emits a non-blocking Android SDK XML version compatibility warning.
