import com.oxygen.gradle_dependencies.BuildVersions

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("oxygen-gradle-dependencies")
}

android {
    compileSdk = BuildVersions.compileSdkVersion

    defaultConfig {
        minSdk = BuildVersions.minSdkVersion
        targetSdk = BuildVersions.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = BuildVersions.kotlinJvmTargetVersion
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }

}

dependencies {
    testImplementation(libs.bundles.tests)
    androidTestImplementation(libs.bundles.uiTests)

    // UI tools
    implementation(libs.bundles.androidxCore)
    implementation(libs.bundles.androidxAppcompat)
    implementation(libs.bundles.androidxAnimations)
    implementation(libs.bundles.androidxCompose)
    implementation(libs.androidxDataBinding)
}
