
dependencies {
    // 1.8.8 is supported but we target 1.9.4 to use PlayerSwapItemEvent if necessary
    compileOnly("org.spigotmc:spigot-api:1.9.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    api(projects.core)
    api(projects.accessItem)
    api("cloud.commandframework:cloud-paper:1.6.2")
    api("me.lucko:commodore:1.13")
    api("net.kyori:adventure-platform-bukkit:4.1.0")
    api("org.bstats:bstats-bukkit:3.0.0")
}

description = "spigot-common"
