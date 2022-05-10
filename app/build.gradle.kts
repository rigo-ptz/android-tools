import com.oxygen.gradle_dependencies.BuildVersions

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("oxygen-gradle-dependencies")
}

android {

    // def libs = rootProject.extensions.getByType(VersionCatalogsExtension).named("libs")
    // val libs = rootProject.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
    ////libs.findVersion("compileSdkVersion").get().toString() as Integer //31

    compileSdk = BuildVersions.compileSdkVersion

    defaultConfig {
        applicationId = "com.oxygen.androidtools"
        minSdk = BuildVersions.minSdkVersion
        targetSdk = BuildVersions.targetSdkVersion
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = BuildVersions.kotlinJvmTargetVersion
    }
}

dependencies {
    implementation(project(":analytics"))

    // Tests
    testImplementation(libs.bundles.tests)
    androidTestImplementation(libs.bundles.uiTests)

    // UI tools
    implementation(libs.bundles.androidxCore)
    implementation(libs.bundles.androidxAppcompat)
    implementation(libs.bundles.googleMaterial)
}
