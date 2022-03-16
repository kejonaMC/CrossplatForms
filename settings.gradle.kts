rootProject.name = "CrossplatForms"

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
        id("net.kyori.indra") version "2.1.1" // multi-release jar
        id("com.github.johnrengelman.shadow") version "7.1.2" // shadowing dependencies
    }
}
