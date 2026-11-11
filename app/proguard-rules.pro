# =========================================================================
# Noor App (com.example.noorapp) - ProGuard / R8 Rules
# =========================================================================

# Preserve readable stack traces for release diagnostics
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Generic / Annotation / Reflection metadata
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

# Honor @Keep annotations
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep <fields>;
    @androidx.annotation.Keep <methods>;
}

# =========================================================================
# App Data Models, DTOs & Room Entities (Gson / Retrofit / Local Cache)
# =========================================================================
-keep class com.example.noorapp.data.remote.dto.** { *; }
-keepclassmembers class com.example.noorapp.data.remote.dto.** { *; }

-keep class com.example.noorapp.domain.model.** { *; }
-keepclassmembers class com.example.noorapp.domain.model.** { *; }

-keep class com.example.noorapp.data.local.entity.** { *; }
-keepclassmembers class com.example.noorapp.data.local.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep enum values()/valueOf() so Gson safely serializes/deserializes enums (e.g. PrayerType)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# =========================================================================
# Retrofit & OkHttp
# =========================================================================
-keep class com.example.noorapp.data.remote.api.** { *; }

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# =========================================================================
# Gson
# =========================================================================
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-dontwarn sun.misc.**

# =========================================================================
# Kotlin, Coroutines, Hilt & BroadcastReceiver
# =========================================================================
-keep class * implements kotlin.Lazy { *; }

-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.flow.**

# Keep Hilt entry points & Manifest-declared components
-keep class * extends android.content.BroadcastReceiver {
    public <init>(...);
}
-keep class com.example.noorapp.App { *; }
-keep class com.example.noorapp.ui.MainActivity { *; }
-keep class com.example.noorapp.service.PrayerAlarmReceiver { *; }
