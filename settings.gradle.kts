rootProject.name = "CrossplatForms"

include(":core")
include(":bungeecord")
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
