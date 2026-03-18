# Keep @Serializable classes and their generated serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.robusthealth.android.data.model.**$$serializer { *; }
-keepclassmembers class com.robusthealth.android.data.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.robusthealth.android.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class com.robusthealth.android.data.network.SupabaseApi { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
