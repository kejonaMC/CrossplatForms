rootProject.name = "CrossplatForms"

include(":core")
include(":access-item")
include(":spigot")
project(":core").projectDir = file("core")
project(":access-item").projectDir = file("access-item")
project(":spigot").projectDir = file("spigot")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version "7.1.0" // shadowing dependencies
    }
}