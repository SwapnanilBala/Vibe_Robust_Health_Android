# Robust Health — Android

Semi-automated fitness planning powered by Supabase, built entirely with Jetpack Compose and Material 3.

> **Status:** Build-verified and optimized — `assembleDebug` passes cleanly on Gradle 8.7 / Kotlin 1.9.24 / AGP 8.5.2.

---

## What We Built

Robust Health is a single-activity Android app that walks users through a guided onboarding flow, generates personalised workout/nutrition/sleep plans via a Supabase REST backend, and provides separate portals for members and credentialed trainers.

### Architecture at a Glance

```
MainActivity
  └─ RobustHealthApp (@Composable)
       └─ NavHost (10 destinations)
            ├─ HomeScreen        – hero section, portal links
            ├─ OnboardingScreen  – 14-field profile intake
            ├─ PlanScreen        – generated plan viewer
            ├─ MemberLoginScreen / MemberPlansScreen
            ├─ TrainerLoginScreen / TrainerClientsScreen
            ├─ PricingScreen / AboutScreen / CheckoutScreen
            └─ (all share ScreenContainer shell + GradientButton)
```

| Layer | Stack |
|-------|-------|
| **UI** | Jetpack Compose · Material 3 · Google Fonts (Sora + Manrope) |
| **Navigation** | Navigation Compose 2.8.4 with sealed-class destinations |
| **State** | ViewModel + `StateFlow<Resource<T>>` per domain slice |
| **Network** | Retrofit 2.11 · OkHttp 4.12 · kotlinx-serialization 1.6.3 |
| **Backend** | Supabase REST (with full demo-mode fallback when credentials are placeholders) |

---

## What We Fixed (17 Build-Blocking Issues)

We took this project from a non-compiling state to a clean `BUILD SUCCESSFUL` by resolving every issue in the dependency chain:

| # | Issue | Resolution |
|---|-------|------------|
| 1 | Missing `gradle-wrapper.jar` | Downloaded Gradle 8.7 distribution, regenerated wrapper via `gradle wrapper` |
| 2 | Missing `local.properties` | Created with correct `sdk.dir` pointing to the Android SDK |
| 3 | `buildConfig = true` not set | Added to `buildFeatures` block so `BuildConfig.SUPABASE_*` fields resolve |
| 4 | Compose Compiler version mismatch | Pinned `kotlinCompilerExtensionVersion = "1.5.14"` (compatible with Kotlin 1.9.24) |
| 5 | `kotlinx-serialization 1.7.3` requires Kotlin 2.0+ | Downgraded to `1.6.3` |
| 6 | `kotlinx-coroutines 1.9.0` requires Kotlin 2.0+ | Downgraded to `1.8.1` |
| 7 | Wrong Retrofit converter import (JakeWharton → retrofit2) | Switched to `retrofit2.converter.kotlinx.serialization` |
| 8 | `KeyboardOptions` import missing | Added `androidx.compose.foundation.text.KeyboardOptions` |
| 9 | `GoogleFont` / `Font()` API misuse | Corrected to `Font(GoogleFont(...), provider)` pattern |
| 10 | `ElevatedCard` deprecated `interactionSource` param | Removed the parameter |
| 11 | `Offset` import from wrong package | Changed to `androidx.compose.ui.geometry.Offset` |
| 12 | Missing `getValue` / `rememberInfiniteTransition` / `animateFloat` imports | Added all required Compose animation imports |
| 13 | `Modifier.weight()` used outside `RowScope` | Extracted into `RowScope`/`ColumnScope` private composables |
| 14 | Missing `INTERNET` permission | Added `<uses-permission>` to AndroidManifest |
| 15 | Redundant self-import in Theme.kt | Removed circular import |
| 16 | `material-icons-extended` not resolving | Added dependency (later optimized to `material-icons-core`) |
| 17 | Missing `ui-text-google-fonts` dependency | Added for Sora/Manrope font provider |

---

## What We Designed (3 Visual Upgrades)

### 1. Hero Layout — HomeScreen
We replaced the flat card list with a full-width gradient hero section featuring:
- **Fuchsia → Violet → Indigo** linear gradient background
- Responsive layout via `BoxWithConstraints` (compact = stacked, wide = side-by-side)
- Frosted `AssistChip` badge, bold headline, two CTA buttons
- Staggered `ElevatedCard` overlays with semi-transparent glass styling
- Decorative radial gradient orb in the top-right corner

### 2. Typography Pairing — Sora + Manrope
We replaced the default Roboto typography with a curated Google Fonts pairing:
- **Sora** (geometric sans-serif) — all heading styles (`displayLarge` through `titleSmall`)
- **Manrope** (humanist sans-serif) — all body and label styles
- Loaded via `GoogleFont` provider with production + staging certificate arrays
- Tuned Material 3 container colors to complement the new type scale

### 3. Micro-Interactions
We added motion throughout the component library:
- **GradientButton** — springy press-scale animation (`dampingRatio = MediumBouncy`) via `graphicsLayer`
- **InfoCard** — `fadeIn` + `scaleIn` entrance animation triggered by `LaunchedEffect`
- **ShimmerPlaceholder** — infinite linear gradient sweep for loading states (used on PlanScreen)
- **ListItemMotion** — `fadeIn` + `slideInVertically` wrapper for list items

---

## What We Optimized (10 Production Hardening Items)

### Build & Dependencies
1. **Enabled R8 + resource shrinking** for release builds — `isMinifyEnabled = true`, `isShrinkResources = true` with proper ProGuard keep rules for Serialization + Retrofit
2. **Replaced `material-icons-extended` with `material-icons-core`** — saves ~10 MB from APK
3. **Bumped `compileSdk` / `targetSdk` to 35** — targets latest Android API level
4. **Removed unused `datastore-preferences`** dependency — eliminates dead weight

### Navigation & State Management
5. **Scoped `collectAsState()` into composable lambdas** — `planState`, `memberState`, `trainerState` are now collected only inside their respective destinations, avoiding unnecessary recomposition of the entire `NavHost`
6. **Hoisted `Json` to a top-level `private val AppJson`** — no longer re-allocated on every recomposition
7. **Fixed initial state values** — `_memberState` and `_trainerState` now start as `Success(empty)` instead of `Loading`, preventing phantom spinners before the user has logged in
8. **Added `launchSingleTop = true`** to every `navigate()` call — prevents duplicate back-stack entries from rapid double-taps

### Components & Network
9. **Fixed `AnimatedVisibility(visible = true)` anti-pattern** — `InfoCard` and `ListItemMotion` now use `LaunchedEffect` to toggle visibility from `false → true` on first composition, so enter animations actually play
10. **Added OkHttp timeouts** — `connectTimeout(15s)`, `readTimeout(30s)`, `writeTimeout(30s)` to prevent indefinite hangs

---

## Project Setup

### Prerequisites
- Android Studio (Arctic Fox or later) with JBR 17
- Android SDK Platform 35
- Gradle 8.7 (wrapper included)

### Configuration
Set your Supabase credentials in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://your-project.supabase.co\"")
buildConfigField("String", "SUPABASE_ANON_KEY", "\"your-anon-key\"")
```

> If left as placeholders, the app runs in **demo mode** with mock data — no backend required.

### Build & Run

```bash
./gradlew assembleDebug     # Debug APK
./gradlew assembleRelease   # Release APK (R8 minified + resource-shrunk)
```

---

## Directory Structure

```
Robust_Health_Android/
├── build.gradle.kts                          # Root – plugin declarations
├── settings.gradle.kts                       # Module registry
├── gradle.properties                         # JVM args, SDK flags
├── gradle/wrapper/                           # Gradle 8.7 wrapper
├── app/
│   ├── build.gradle.kts                      # App module – deps, compose, R8
│   ├── proguard-rules.pro                    # Serialization + Retrofit keep rules
│   └── src/main/
│       ├── AndroidManifest.xml               # INTERNET permission, single activity
│       ├── java/com/robusthealth/android/
│       │   ├── MainActivity.kt               # Entry point
│       │   ├── data/
│       │   │   ├── model/Models.kt           # @Serializable data classes
│       │   │   ├── network/SupabaseApi.kt    # Retrofit interface
│       │   │   ├── network/SupabaseClient.kt # OkHttp + Retrofit singleton
│       │   │   └── repository/SupabaseRepository.kt  # Demo-fallback repository
│       │   ├── navigation/
│       │   │   ├── Destinations.kt           # Sealed class routes
│       │   │   ├── MainViewModel.kt          # StateFlow-based ViewModel
│       │   │   └── RobustHealthApp.kt        # NavHost with scoped state
│       │   └── ui/
│       │       ├── components/ScreenShell.kt # ScreenContainer, GradientButton, InfoCard, Shimmer
│       │       ├── screens/                  # HomeScreen, Onboarding, Plan, Member, Trainer, Marketing
│       │       └── theme/                    # Color.kt, Theme.kt, Typography.kt (Sora + Manrope)
│       └── res/
│           ├── raw/                          # 10 .mp4 video assets
│           └── values/                       # strings, themes, font_certs
```

---

## Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| Language | Kotlin | 1.9.24 |
| Build | AGP / Gradle | 8.5.2 / 8.7 |
| UI | Jetpack Compose (BOM) | 2024.09.01 |
| Design System | Material 3 | BOM-managed |
| Typography | Google Fonts (Sora, Manrope) | via `ui-text-google-fonts` |
| Navigation | Navigation Compose | 2.8.4 |
| Lifecycle | ViewModel + Runtime Compose | 2.8.6 |
| Serialization | kotlinx-serialization-json | 1.6.3 |
| Networking | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| Images | Coil Compose | 2.7.0 |
| Backend | Supabase REST API | — |
| Min SDK | Android 7.0 | API 24 |
| Target SDK | Android 15 | API 35 |
