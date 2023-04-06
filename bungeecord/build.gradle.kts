import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    compileOnly("com.github.SpigotMC.BungeeCord:bungeecord-proxy:e717676") // For getting skins (dependency through jitpack)
    api("cloud.commandframework:cloud-bungee:1.8.3")
    api("net.kyori:adventure-platform-bungeecord:4.2.0")
    implementation("org.bstats:bstats-bungeecord:3.0.2")
    api(projects.proxy)
    api(projects.core)
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("com.google.inject", "dev.kejona.crossplatforms.shaded.guice")
            relocate("cloud.commandframework", "dev.kejona.crossplatforms.shaded.cloud")
            relocate("net.kyori", "dev.kejona.crossplatforms.shaded.kyori")
            relocate("org.spongepowered.configurate", "dev.kejona.crossplatforms.shaded.configurate")
            // Used by cloud and configurate
            relocate("io.leangen.geantyref", "dev.kejona.crossplatforms.shaded.typetoken")
            relocate("org.bstats", "dev.kejona.crossplatforms.shaded.bstats")
        }
        exclude {
                e ->
            val name = e.name
            name.startsWith("com.mojang") // all available on bungee
            || name.startsWith("org.yaml")
            // Guice must be relocated, everything else is available
            || (name.startsWith("com.google") && !name.startsWith("com.google.inject"))
            || name.startsWith("javax.inject")
        }
    }

    archiveFileName.set("CrossplatForms-BungeeCord.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "bungeecord"
