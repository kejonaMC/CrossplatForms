rootProject.name = "CrossplatForms"

include(":core")
include(":access-item")
include("spigot-common")
include(":spigot")
include("spigot-legacy")
project(":core").projectDir = file("core")
project(":access-item").projectDir = file("access-item")
project(":spigot-common").projectDir = file("spigot-common")
project(":spigot").projectDir = file("spigot")
project(":spigot-legacy").projectDir = file("spigot-legacy")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version "7.1.2" // shadowing dependencies
    }
}
