/*
package com.oxygen.gradle_dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

fun Project.version(key: String): String = this.extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion(key)
    .get()
    .requiredVersion

fun Project.versionInt(key: String) = version(key).toInt()

val Project.KOTLIN_JVM_TARGET_VERSION get() = version("kotlinJvmTargetVersion")
val Project.ANDROID_COMPILE_SDK_VERSION get() = versionInt("compileSdkVersion")
val Project.ANDROID_MIN_SDK_VERSION get() = versionInt("minSdkVersion")
val Project.ANDROID_TARGET_SDK_VERSION get() = versionInt("targetSdkVersion")
*/
