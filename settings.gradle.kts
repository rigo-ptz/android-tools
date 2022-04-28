pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    // Fails building process in case any module declares own repository
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("./libs.versions.toml"))
        }
    }
}

rootProject.name = "AndroidTools"
includeBuild("gradle-dependencies")
include(":app")
include(":ktx-ui")
include(":network")
