
plugins {
    id("net.kyori.indra")
    id("java-test-fixtures")
}

sourceSets {
    main {
        multirelease {
            alternateVersions(16)
        }
    }

    create("java16")
}

// Add more configurations here as necessary
val java16Implementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    testImplementation("org.geysermc.cumulus:cumulus:1.1.1-SNAPSHOT") // needed for testing button components
    testImplementation("com.google.code.gson:gson:2.8.6") // needed for cumulus

    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT") {
        exclude(group = "com.google.code.gson", module = "gson")
    }

    api("cloud.commandframework:cloud-core:1.7.1")
    api("cloud.commandframework:cloud-minecraft-extras:1.7.1")
    api("net.kyori:adventure-api:4.11.0")
    api("net.kyori:adventure-text-serializer-legacy:4.11.0")
    api("net.kyori:adventure-text-serializer-gson:4.11.0")
    api("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    api("org.spongepowered:configurate-extra-guice:4.2.0-SNAPSHOT")
    api("com.google.code.gson:gson:2.3.1") // version provided by spigot 1.8.8
    api("com.google.inject:guice:5.1.0")
    api("org.bstats:bstats-base:3.0.0")

    // Required because source and unshaded jars are all mixed up on the opencollab repo currently
    val baseApi = "2.1.0-20221012.212632-20"
    val geyserApi = "2.1.0-20221012.212634-20"
    val geyserCore = "2.1.0-20221012.212644-20"

    // dependencies for java16 sources (optionally used at runtime)
    java16Implementation("org.geysermc:api:$baseApi")
    java16Implementation("org.geysermc.geyser:api:$geyserApi")
    java16Implementation("org.geysermc.geyser:core:$geyserCore") {
        isTransitive = false
    }
}

description = "core"
