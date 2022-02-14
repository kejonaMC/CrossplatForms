


plugins {
    id("com.github.johnrengelman.shadow")
}


repositories {
    mavenLocal()
    mavenCentral()

    maven("https://libraries.minecraft.net/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    compileOnly("org.projectlombok:lombok:1.18.22")
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    compileOnly("org.geysermc:core:2.0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.1.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    api("cloud.commandframework:cloud-paper:1.6.1")
    api("cloud.commandframework:cloud-minecraft-extras:1.6.1")
    api("me.lucko:commodore:1.9")
    api("net.kyori:adventure-platform-bukkit:4.0.1")
    api("org.spongepowered:configurate-yaml:4.1.2")
}