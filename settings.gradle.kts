rootProject.name = "CrossplatForms"

include(":build-logic")
include(":core")
include(":bungeecord")
include("velocity")
include(":access-item")
include("spigot-common")
include(":spigot")
include(":spigot-legacy")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version "7.1.2" // shadowing dependencies
    }
}
