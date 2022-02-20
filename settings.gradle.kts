rootProject.name = "CrossplatForms"

include(":core")
include(":spigot")
project(":core").projectDir = file("core")
project(":spigot").projectDir = file("spigot")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version "7.1.0" // shadowing dependencies
    }
}