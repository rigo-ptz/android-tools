plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    // kotlin("jvm") version "1.5.31"
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

group = "com.oxygen.androidtools.gradle"
version = "SNAPSHOT"

// val kotlinVersion = "1.6.20"

dependencies {
    implementation(gradleApi())
    // implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}

gradlePlugin {
    plugins.register("Gradle extensions") {
        id = "oxygen-gradle-dependencies"
        implementationClass = "com.oxygen.gradle_dependencies.DependenciesPlugin"
    }
}
