enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CrossplatForms"

include(":core")
include(":proxy")
include(":bungeecord")
include(":velocity")
include(":access-item")
include(":spigot-common")
include(":spigot")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("net.kyori.indra.git") version "3.0.1" // getting branch and commit info at compile time
        id("net.kyori.indra") version "3.0.1" // multi-release jar for referencing Java 16 geyser api
        id("com.github.johnrengelman.shadow") version "7.1.2" // shadowing dependencies
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://repo.opencollab.dev/main/") // geyser etc
        maven("https://jitpack.io") // fixes issue with Cloudburst Protocol that geyser depends on
        maven("https://libraries.minecraft.net/") // brigadier

        maven("https://oss.sonatype.org/content/repositories/snapshots") // bungeecord, spigot
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // spigot
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
        maven("https://repo.codemc.org/repository/maven-public/") // NBT api

        maven("https://mvn.exceptionflug.de/repository/exceptionflug-public/") // protocolize for proxies
        maven("https://nexus.velocitypowered.com/repository/maven-public/") // velocity
    }
}
include("spigotcompat:v1_8_R3")
findProject(":spigotcompat:v1_8_R3")?.name = "v1_8_R3"
include("spigotcompat:common")
findProject(":spigotcompat:common")?.name = "common"
include("spigotcompat:all")
findProject(":spigotcompat:all")?.name = "all"
include("spigotcompat:v1_14_R1")
findProject(":spigotcompat:v1_14_R1")?.name = "v1_14_R1"
include("spigotcompat:v1_13_R2")
findProject(":spigotcompat:v1_13_R2")?.name = "v1_13_R2"
include("spigotcompat:v1_12_R1")
findProject(":spigotcompat:v1_12_R1")?.name = "v1_12_R1"
include("spigotcompat:v1_9_R2")
findProject(":spigotcompat:v1_9_R2")?.name = "v1_9_R2"
