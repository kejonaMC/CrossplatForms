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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://repo.opencollab.dev/main/") // geyser etc
        maven("https://jitpack.io") // fixes issue with Cloudburst Protocol that geyser depends on
        maven("https://oss.sonatype.org/content/repositories/snapshots") // bungeecord, spigot
        maven("https://nexus.velocitypowered.com/repository/maven-public/") // velocity
        maven("https://libraries.minecraft.net/") // brigadier
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // spigot
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
        maven("https://repo.codemc.org/repository/maven-public/") // NBT api
    }
}
