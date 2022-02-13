rootProject.name = "CrossplatForms"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version "7.1.0" // shadowing dependencies
    }
}
