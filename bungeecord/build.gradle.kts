import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
    id("dev.projectg.crossplatforms.shadow-conventions")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
    api("cloud.commandframework:cloud-bungee:1.6.2")
    api("net.kyori:adventure-platform-bungeecord:4.1.0")
    implementation("org.bstats:bstats-bungeecord:3.0.0")
    api(project(":proxy"))
    api(project(":core"))
}

tasks.withType<ShadowJar> {
    archiveFileName.set("CrossplatForms-BungeeCord.jar")
}

relocate("cloud.commandframework")
relocate("com.google.inject")
relocate("net.kyori")
relocate("org.spongepowered.configurate")
relocate("io.leangen.geantyref") // used by cloud and configurate

exclude("com.google.code.gson")
exclude("com.google.guava")
exclude("org.yaml", "snakeyaml")

description = "bungeecord"
