# Changelog

## Build-fix and hardening pass

Took the project from not compiling to a clean `assembleDebug`, then reworked the visuals and
hardened the build. Toolchain at the time: Gradle 8.7, AGP 8.5.2, Kotlin 1.9.24. It has since
moved to Gradle 8.13 and AGP 8.13.2.

### Fixed: 17 build-blocking issues

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


### Designed: 3 visual upgrades

#### 1. Hero Layout — HomeScreen
We replaced the flat card list with a full-width gradient hero section featuring:
- **Fuchsia → Violet → Indigo** linear gradient background
- Responsive layout via `BoxWithConstraints` (compact = stacked, wide = side-by-side)
- Frosted `AssistChip` badge, bold headline, two CTA buttons
- Staggered `ElevatedCard` overlays with semi-transparent glass styling
- Decorative radial gradient orb in the top-right corner

#### 2. Typography Pairing — Sora + Manrope
We replaced the default Roboto typography with a curated Google Fonts pairing:
- **Sora** (geometric sans-serif) — all heading styles (`displayLarge` through `titleSmall`)
- **Manrope** (humanist sans-serif) — all body and label styles
- Loaded via `GoogleFont` provider with production + staging certificate arrays
- Tuned Material 3 container colors to complement the new type scale

#### 3. Micro-Interactions
We added motion throughout the component library:
- **GradientButton** — springy press-scale animation (`dampingRatio = MediumBouncy`) via `graphicsLayer`
- **InfoCard** — `fadeIn` + `scaleIn` entrance animation triggered by `LaunchedEffect`
- **ShimmerPlaceholder** — infinite linear gradient sweep for loading states (used on PlanScreen)
- **ListItemMotion** — `fadeIn` + `slideInVertically` wrapper for list items


### Optimized: 10 hardening items

#### Build & Dependencies
1. **Enabled R8 + resource shrinking** for release builds — `isMinifyEnabled = true`, `isShrinkResources = true` with proper ProGuard keep rules for Serialization + Retrofit
2. **Replaced `material-icons-extended` with `material-icons-core`** — saves ~10 MB from APK
3. **Bumped `compileSdk` / `targetSdk` to 35** — targets latest Android API level
4. **Removed unused `datastore-preferences`** dependency — eliminates dead weight

#### Navigation & State Management
5. **Scoped `collectAsState()` into composable lambdas** — `planState`, `memberState`, `trainerState` are now collected only inside their respective destinations, avoiding unnecessary recomposition of the entire `NavHost`
6. **Hoisted `Json` to a top-level `private val AppJson`** — no longer re-allocated on every recomposition
7. **Fixed initial state values** — `_memberState` and `_trainerState` now start as `Success(empty)` instead of `Loading`, preventing phantom spinners before the user has logged in
8. **Added `launchSingleTop = true`** to every `navigate()` call — prevents duplicate back-stack entries from rapid double-taps

#### Components & Network
9. **Fixed `AnimatedVisibility(visible = true)` anti-pattern** — `InfoCard` and `ListItemMotion` now use `LaunchedEffect` to toggle visibility from `false → true` on first composition, so enter animations actually play
10. **Added OkHttp timeouts** — `connectTimeout(15s)`, `readTimeout(30s)`, `writeTimeout(30s)` to prevent indefinite hangs
