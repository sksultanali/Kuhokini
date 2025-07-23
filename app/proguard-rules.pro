# ================= CORE CONFIGURATION =================
   -dontobfuscate
   -optimizations !code/simplification/arithmetic,!code/allocation/variable
   -keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
   -keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
   -keepattributes Exceptions  # Preserve exception info for stack traces

   # ================= DATASTORE & PROTOBUF =================
   -keep class androidx.datastore.** { *; }
   -keep class com.google.protobuf.** { *; }
   -keep class com.kuhokini.proto.** { *; }

   # ================= KOTLIN & COROUTINES =================
   -keep class kotlinx.coroutines.** { *; }
   -keep class kotlin.coroutines.Continuation { *; }
   -keep class kotlinx.coroutines.android.** { *; }
   -dontwarn kotlinx.coroutines.**

   # ================= ANDROIDX =================
   -keep class androidx.lifecycle.** { *; }
   -keep class androidx.concurrent.** { *; }
   -keep class androidx.emoji2.** { *; }
   -keep class androidx.startup.** { *; }

   # ================= WINDOW MANAGEMENT =================
   -keep class android.view.Window { *; }
   -keep class android.view.WindowManager { *; }
   -keep class android.view.ViewRootImpl.** { *; }

   # ================= FIREBASE =================
   -keep class com.google.firebase.** { *; }
   -keep class com.google.android.gms.** { *; }
   -dontwarn com.google.firebase.**
   -dontwarn com.google.android.gms.**

   # ================= RETROFIT/OKHTTP =================
   -keep class okhttp3.** { *; }
   -keep class okio.** { *; }
   -keep class retrofit2.** { *; }
   -dontwarn okhttp3.**
   -dontwarn okio.**
   -dontwarn retrofit2.**
   -keepattributes *Annotation*
   -keepclasseswithmembers interface * {
       @retrofit2.http.* <methods>;
   }
   # Enhanced Retrofit rules for callbacks, adapters, and response types
   -keep class retrofit2.DefaultCallAdapterFactory { *; }
   -keep class retrofit2.DefaultCallAdapterFactory$ExecutorCallbackCall { *; }
   -keep class retrofit2.Callback { *; }
   -keep class retrofit2.Response { *; }
   -keep class retrofit2.Call { *; }

   # ================= GSON (for Retrofit JSON parsing) =================
   -keep class com.google.gson.** { *; }
   -keepattributes Signature
   -keepattributes *Annotation*
   # Preserve all fields and methods in classes used for JSON serialization
   -keep class * {
       public private protected *;
   }

   # ================= RAZORPAY =================
   -keep class com.razorpay.** { *; }
   -dontwarn com.razorpay.**
   -keepclassmembers class * {
       @android.webkit.JavascriptInterface <methods>;
   }

   # ================= APPLICATION SPECIFIC =================
   -keep class com.kuhokini.** { *; }
   -keep class com.kuhokini.models.** { *; }  # Explicitly keep model classes
   -keep class com.kuhokini.BottomBar.** { *; }  # Keep BottomBar classes, including HomeFragment
   -keep class com.kuhokini.ApiService.** { *; }  # Keep API service interface
   # If missing_rules.txt suggests additional classes, add them here, e.g.:
   # -keep class com.kuhokini.models.HomeStay { *; }
   # -keep class com.kuhokini.models.Location { *; }
   -keep class com.kuhokini.** { *; }
   -keep class com.kuhokini.models.** { *; }
   -keep class com.kuhokini.datastore.** { *; }

   # ================= SERIALIZATION =================
   -keepclassmembers class * implements java.io.Serializable {
       static final long serialVersionUID;
       private static final java.io.ObjectStreamField[] serialPersistentFields;
       private void writeObject(java.io.ObjectOutputStream);
       private void readObject(java.io.ObjectInputStream);
       java.lang.Object writeReplace();
       java.lang.Object readResolve();
   }

   # ================= NATIVE METHODS =================
   -keepclasseswithmembernames class * {
       native <methods>;
   }

   # ================= VIEW BINDING =================
   -keepclassmembers class * extends android.view.View {
       void set*(***);
       *** get*();
   }

   # ================= ACTIVITY/FRAGMENT =================
   -keep public class * extends android.app.Activity
   -keep public class * extends android.app.Fragment
   -keep public class * extends androidx.fragment.app.Fragment
   -keep public class * extends android.app.Application
   -keep public class * extends android.app.Service

   -dontwarn org.apache.http.**
   -dontwarn android.net.**
   -keep class org.apache.** {*;}
   -keep class org.apache.http.** { *; }
   -dontwarn java.lang.management.ManagementFactory
   -dontwarn java.lang.management.RuntimeMXBean


   # Please add these rules to your existing keep rules in order to suppress warnings.
   # This is generated automatically by the Android Gradle plugin.
   -dontwarn javax.servlet.ServletContextEvent
   -dontwarn javax.servlet.ServletContextListener
   -dontwarn org.apache.avalon.framework.logger.Logger
   -dontwarn org.apache.log.Hierarchy
   -dontwarn org.apache.log.Logger
   -dontwarn org.apache.log4j.Level
   -dontwarn org.apache.log4j.Logger
   -dontwarn org.apache.log4j.Priority