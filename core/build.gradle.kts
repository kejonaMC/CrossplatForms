
repositories {
    maven("https://repo.opencollab.dev/main/")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.geysermc.cumulus:cumulus:1.0-SNAPSHOT") // needed for testing button components
    testImplementation("com.google.code.gson:gson:2.9.0") // needed for cumulus

    compileOnly("org.geysermc:geyser-api:2.0.1-SNAPSHOT")
    compileOnly("org.geysermc:core:2.0.1-SNAPSHOT") {
        isTransitive = false // exclude all the junk we won't and can't use
    }
    compileOnly("org.geysermc.floodgate:api:2.1.0-SNAPSHOT")
    api("cloud.commandframework:cloud-core:1.6.1")
    api("cloud.commandframework:cloud-minecraft-extras:1.6.1")
    api("net.kyori:adventure-api:4.9.3")
    api("net.kyori:adventure-text-serializer-legacy:4.9.3")
    api("org.spongepowered:configurate-yaml:4.1.2")
}

description = "core"
