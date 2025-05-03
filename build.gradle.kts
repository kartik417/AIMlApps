// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // Apply plugin versions for the entire project
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false // Required for Firebase
}

buildscript {
    repositories {
        google()  // Google's repository for Firebase and other services
        mavenCentral()  // Central repository for dependencies
    }

    dependencies {
        // Android Gradle Plugin
        classpath ("com.android.tools.build:gradle:7.2.0")  // Use the latest stable version
        // Kotlin plugin classpath
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")  // Ensure compatibility with your Kotlin version
        // Google services classpath (for Firebase and others)
        classpath ("com.google.gms:google-services:4.3.10")  // Ensure you're using a compatible version
    }
}

