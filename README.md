# ME.App — API Directory (Android)

Native Android wrapper around the
[**API_directory**](https://github.com/BEKO2210/API_directory) static site
(`https://beko2210.github.io/API_directory/`). Built in the spirit of
[shiahonb777/web-to-app](https://github.com/shiahonb777/web-to-app):
Kotlin + Jetpack Compose + Material 3 + WebView, no Cordova/Capacitor.

## Stack

- Kotlin 2.0, JDK 17
- Android Gradle Plugin 8.6
- Jetpack Compose (BOM 2024.10) + Material 3
- AndroidX SplashScreen + SwipeRefreshLayout
- `minSdk 23` (Android 6.0), `targetSdk 34`

## Features

- WebView wrapping the API_directory site with JS, DOM storage, and zoom enabled
- External links (e.g. individual API docs) open in the system browser
- Pull-to-refresh and progress bar
- Hardware Back navigates the WebView history
- Light/dark theme that follows the system, with Material You dynamic color on Android 12+
- Splash screen with a custom logo
- Adaptive launcher icon (incl. monochrome for themed icons)

## Build

### Local

```bash
git clone https://github.com/BEKO2210/me.app.git
cd me.app
gradle wrapper --gradle-version 8.10.2 --distribution-type bin   # one-off, generates gradlew(.bat) + jar
./gradlew :app:assembleDebug                                     # APK -> app/build/outputs/apk/debug/
```

Open the project in Android Studio (Hedgehog or newer) for the standard
build/run experience.

### CI

`.github/workflows/android.yml` builds a debug APK on every push to `main`
or `claude/**` and uploads it as the `api-directory-debug-apk` artifact.

## Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/com/beko2210/apidirectory/
│   ├── ApiDirectoryApp.kt
│   ├── MainActivity.kt
│   ├── ui/theme/         # Color.kt, Theme.kt
│   └── web/              # ApiDirectoryWebView.kt, WebViewState.kt
└── res/                  # icons, themes, splash, network config
```

The home URL lives in `MainActivity.HOME_URL` — change it there to point the
shell at a different site.

## Credits

- Content: [BEKO2210/API_directory](https://github.com/BEKO2210/API_directory)
- Approach inspired by:
  [shiahonb777/web-to-app](https://github.com/shiahonb777/web-to-app)
