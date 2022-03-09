
repositories {
    maven("https://libraries.minecraft.net/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    api(project(":core"))
    api(project(":access-item"))
    api("cloud.commandframework:cloud-paper:1.6.2")
    api("me.lucko:commodore:1.13")
    api("net.kyori:adventure-platform-bukkit:4.1.0")
}

description = "spigot-common"
