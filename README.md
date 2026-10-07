# Robust Health — Android

Native Android client for [Robust Health](https://app.robusthealth.in), a fitness platform that turns a
member's profile into weekly workout, nutrition and sleep plans. Built with Jetpack Compose and
Material 3 on a Supabase backend.

It runs without a backend: leave the Supabase credentials as placeholders and the app switches to
**demo mode** with mock data.

## What it does

- **Guided onboarding:** a 14-field profile intake.
- **Generated plans:** workout, nutrition and sleep, fetched from Supabase or mocked in demo mode.
- **Two portals:** members see their plans; credentialed trainers see their clients.
- **Marketing flow:** home hero, pricing, about and checkout screens, with bundled MP4 clips.

```
MainActivity
  └─ RobustHealthApp (@Composable)
       └─ NavHost (10 destinations)
            ├─ HomeScreen        – hero section, portal links
            ├─ OnboardingScreen  – 14-field profile intake
            ├─ PlanScreen        – generated plan viewer
            ├─ MemberLoginScreen / MemberPlansScreen
            ├─ TrainerLoginScreen / TrainerClientsScreen
            └─ PricingScreen / AboutScreen / CheckoutScreen
```

| Layer | Approach |
|---|---|
| UI | Jetpack Compose, Material 3, Sora + Manrope via downloadable Google Fonts |
| Navigation | Navigation Compose with sealed-class destinations, `launchSingleTop` everywhere |
| State | One ViewModel exposing `StateFlow<Resource<T>>` per domain slice, collected per destination |
| Network | Retrofit + OkHttp (15 s connect / 30 s read and write timeouts) + kotlinx-serialization |
| Backend | Supabase REST, with a demo-mode fallback in `SupabaseRepository` |
| Release | R8 minification and resource shrinking, with keep rules for serialization and Retrofit |

## Run it

**Needs:** a current Android Studio (AGP 8.13), JDK 17 and Android SDK Platform 35. The Gradle
8.13 wrapper is included.

```bash
./gradlew assembleDebug     # debug APK
./gradlew assembleRelease   # release APK, R8-minified and resource-shrunk
```

To use a real backend, set the two fields in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://your-project.supabase.co\"")
buildConfigField("String", "SUPABASE_ANON_KEY", "\"your-anon-key\"")
```

Better still, read them from `local.properties`, so a real key never lands in git.

## Structure

```
app/src/main/java/com/robusthealth/android/
├── MainActivity.kt
├── data/
│   ├── model/Models.kt                  @Serializable data classes
│   ├── network/SupabaseApi.kt           Retrofit interface
│   ├── network/SupabaseClient.kt        OkHttp + Retrofit singleton
│   └── repository/SupabaseRepository.kt demo-mode fallback lives here
├── navigation/                          routes, ViewModel, NavHost
└── ui/
    ├── components/ScreenShell.kt        ScreenContainer, GradientButton, InfoCard, Shimmer
    ├── screens/                         Home, Onboarding, Plan, Member, Trainer, Marketing
    └── theme/                           colours, Sora + Manrope typography
```

## Stack

| | Version |
|---|---|
| Kotlin | 1.9.24 (Compose compiler 1.5.14) |
| Android Gradle Plugin / Gradle | 8.13.2 / 8.13 |
| Compose BOM | 2024.09.01 |
| Navigation Compose | 2.8.4 |
| Lifecycle | 2.8.6 |
| kotlinx-serialization / coroutines | 1.6.3 / 1.8.1 |
| Retrofit / OkHttp | 2.11.0 / 4.12.0 |
| Coil | 2.7.0 |
| Min / target SDK | 24 (Android 7.0) / 35 (Android 15) |

The history of getting this from a non-compiling state to a clean, hardened build is in
[CHANGELOG.md](CHANGELOG.md).
