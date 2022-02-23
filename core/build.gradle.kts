
repositories {
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    compileOnly("org.geysermc:core:2.0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.1.0-SNAPSHOT")
    api("cloud.commandframework:cloud-core:1.6.1")
    api("cloud.commandframework:cloud-minecraft-extras:1.6.1")
    api("net.kyori:adventure-api:4.9.3")
    api("org.spongepowered:configurate-yaml:4.1.2")
}

description = "core"