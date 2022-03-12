
repositories {
    maven("https://repo.opencollab.dev/main/")
    maven("https://jitpack.io") // avoids weird transitive dependency error with Geyser core
}

dependencies {
    testImplementation("org.geysermc.cumulus:cumulus:1.0-SNAPSHOT") // needed for testing button components
    testImplementation("com.google.code.gson:gson:2.8.6") // needed for cumulus

    compileOnly("org.geysermc:core:2.0.2-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.1.0-SNAPSHOT")
    api("cloud.commandframework:cloud-core:1.6.2")
    api("cloud.commandframework:cloud-minecraft-extras:1.6.2")
    api("net.kyori:adventure-api:4.10.1")
    api("net.kyori:adventure-text-serializer-legacy:4.10.1")
    api("org.spongepowered:configurate-yaml:4.1.2")
}

description = "core"
