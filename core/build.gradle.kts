
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

    // Nullability annotations. todo: move to something else
    testImplementation("com.google.code.findbugs:jsr305:3.0.2")
    api("com.google.code.findbugs:jsr305:3.0.2")

    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT") { isTransitive = false }
    compileOnly("net.luckperms:api:5.4") { isTransitive = false }

    api("org.bstats:bstats-base:3.0.0")
    api("cloud.commandframework:cloud-core:1.8.0")
    api("cloud.commandframework:cloud-minecraft-extras:1.8.0")
    api("net.kyori:adventure-api:4.12.0")
    api("net.kyori:adventure-text-serializer-legacy:4.12.0")
    api("net.kyori:adventure-text-serializer-gson:4.12.0") {
        // This is required or else it overrides the version we explicitly define below
        exclude(group = "com.google.code.gson", module = "gson")
    }

    api("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    api("org.spongepowered:configurate-extra-guice:4.2.0-SNAPSHOT")

    api("com.google.inject:guice:5.0.1") {
       exclude(group = "com.google.guava", module = "guava") // Provides a newer version than provided by server platforms
    }

    api("com.google.guava:guava") {
        version {
            prefer("21.0") // Provided by Spigot 1.14, Velocity, BungeeCord. On 1.13 and below, guava is shaded.
        }
    }
    api("com.google.code.gson:gson") {
        version {
            prefer("2.3.1") // lowest version, provided by spigot 1.8.8
        }
    }

    // Required because source and unshaded jars are all mixed up on the opencollab repo currently
    val baseApi = "2.1.0-20221211.182143-60"
    val geyserApi = "2.1.0-20221211.182145-60"
    val geyserCore = "2.1.0-20221211.182157-60"

    // dependencies for java16 sources (optionally used at runtime)
    java16Implementation("org.geysermc:api:$baseApi") { isTransitive = false }
    java16Implementation("org.geysermc.geyser:api:$geyserApi") { isTransitive = false }
    java16Implementation("org.geysermc.geyser:core:$geyserCore") { isTransitive = false }
}

description = "core"
