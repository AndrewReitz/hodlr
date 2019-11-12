rootProject.name = "hodlr"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        jcenter()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.squareup.sqldelight") {
                useModule("com.squareup.sqldelight:gradle-plugin:${requested.version}")
            }
        }
    }
}
