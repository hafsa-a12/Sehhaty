# Gson : conserver les modèles sérialisés
-keep class com.sante.app.model.** { *; }
-keep class com.sante.app.api.AppointmentRequest { *; }
# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-dontwarn retrofit2.**
