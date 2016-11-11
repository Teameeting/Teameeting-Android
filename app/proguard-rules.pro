# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 7
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmember class * {
    native <methods>;
}

-keepclasseswithmember class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmember class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepattributes *Annotation*
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes SourceFile,LineNumberTable

-keep class **.R$* { *; }
#-libraryjars  libs/android-support-v4.jar
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

#工程中含有第三方jar包
#-libraryjars libs/anyrtc-1.0.2.jar
#-libraryjars libs/jpush-android-2.1.0.jar
#-libraryjars libs/libammsdk.jar
#-libraryjars libs/msgclient_r1.0.1.jar
#-libraryjars libs/nineoldandroids-2.4.0.jar
#libraryjars libs/pgyer_sdk_2.2.2.jar

#项目里面包含的包也不能混淆
-keep class android.support.v4.** {*;}
-dontwarn android.support.v4.**

-keep class com.ypy.eventbus.** {*;}
-dontwarn com.ypy.eventbus.**
-keepclassmembers class ** {
    public void onEvent*(**);
}

-keep class org.anyrtc.** {*;}
-dontwarn org.anyrtc.**

-keep class org.webrtc.** {*;}
-dontwarn org.webrtc.**

-keep class cn.jpush.** {*;}
-dontwarn cn.jpush.**

-keep class com.google.** {*;}
-dontwarn com.google.**

 -keep class com.tencent.mm.** {*;}
-dontwarn com.tencent.mm.**

-keep class org.dync.teameeting.sdkmsgclient.** {*;}
-dontwarn org.dync.teameeting.sdkmsgclient.**

-keep class com.nineoldandroids.animation.** {*;}
-dontwarn com.nineoldandroids.animation.**

-keep class com.pgyersdk.** {*;}
-dontwarn com.pgyersdk.**

-keep class org.dync.teameeting.bean.** {*;}
-dontwarn org.dync.teameeting.bean.**

# # -------------------------------------------

# #  ######## greenDao混淆  ##########

# # -------------------------------------------

-keep class de.greenrobot.dao.** {*;}

-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

## ----------------------------------

##   ########## Gson混淆    ##########

## ----------------------------------

-keepattributes Signature

-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.examples.android.model.** { *; }
