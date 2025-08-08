import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")

    api(projects.core)
    api(projects.accessItem)
    api("cloud.commandframework:cloud-paper:1.8.3")
    api("me.lucko:commodore:2.2")
    api("net.kyori:adventure-platform-bukkit:4.3.0")
    api("org.bstats:bstats-bukkit:3.0.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.mojang:authlib:1.5.21")
    implementation("com.google.code.gson:gson:2.10.1") // belangrijk voor shading
}

tasks.withType<ShadowJar> {
    relocate("com.google.inject", "dev.kejona.crossplatforms.shaded.guice")
    relocate("com.google.common", "dev.kejona.crossplatforms.shaded.google.common")
    relocate("cloud.commandframework", "dev.kejona.crossplatforms.shaded.cloud")
    relocate("me.lucko.commodore", "dev.kejona.crossplatforms.shaded.commodore")
    relocate("net.kyori", "dev.kejona.crossplatforms.shaded.kyori")
    relocate("org.spongepowered.configurate", "dev.kejona.crossplatforms.shaded.configurate")
    relocate("io.leangen.geantyref", "dev.kejona.crossplatforms.shaded.typetoken")
    relocate("org.bstats", "dev.kejona.crossplatforms.shaded.bstats")
    relocate("de.tr7zw.changeme.nbtapi", "dev.kejona.crossplatforms.shaded.nbtapi")
    relocate("de.tr7zw.annotations", "dev.kejona.crossplatforms.shaded.tr7zw.annotations")
    relocate("com.google.gson", "dev.kejona.crossplatforms.shaded.gson")
    relocate("org.yaml.snakeyaml", "dev.kejona.crossplatforms.shaded.snakeyaml")

    archiveFileName.set("CrossplatForms-Spigot.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "spigot module"
