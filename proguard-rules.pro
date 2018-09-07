# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidSDK\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#//保留注解参数
#-keepattributes *Annotation*
#-keep class com.gl.annaop.** { *; }

## ----------------------------------
##   ##########   ##########
## ----------------------------------
-keepattributes *Annotation*
-dontwarn com.gl.annaop.**
-keep class com.gl.annaop.** { *; }
-keep interface com.gl.annaop.** { *; }
-keep class org.aspectj.** { *; }

-keepclassmembers class ** {
    @com.gl.annaop.annotatio.PermissionAnnotation *;
    @org.aspectj.lang.annotation.Aspect *;
    @org.aspectj.lang.annotation.Pointcut *;
    @org.aspectj.lang.annotation.Around *;
    @org.aspectj.lang.annotation.Around *;
}
-keepnames @org.aspectj.lang.annotation.Aspect class * {
    @org.aspectj.lang.annotation.Pointcut public *;
    @org.aspectj.lang.annotation.Pointcut Around *;
}

#-keep class * { @com.gl.annaop.annotation.PermissionAnnotation <methods>; }
#-keep class com.gl.annaop.aopinject.** { *; }
#-keep class com.gl.annaop.annotation.** { *; }
#-keepclassmembers class * { @org.aspectj.lang.annotation.Aspect <methods>; }
#
#-keepclassmembers class * { @org.aspectj.lang.annotation.Aspect <methods>; }
#
#-keep @com.gl.annaop.annotation.PermissionAnnotation class * {*;}
#-keep class * {
#@com.gl.annaop.annotation.PermissionAnnotation <methods>;
#}
#-keep class * extends java.lang.annotation.Annotation { *; }
#-keep interface * extends java.lang.annotation.Annotation { *; }
##-----------------AOP----------------------------------------------------------------------------------
#-adaptclassstrings
#-keepattributes InnerClasses, EnclosingMethod, Signature, *Annotation*
#-keepnames @org.aspectj.lang.annotation.Aspect class * {
#    ajc* <methods>;
#}
##---------------------------------------------------------------------------------------------------
