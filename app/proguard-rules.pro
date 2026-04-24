# Keep WebView JavaScript bridge methods if added later.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Standard Compose / Kotlin metadata.
-keep class kotlin.Metadata { *; }
-dontwarn kotlinx.**
