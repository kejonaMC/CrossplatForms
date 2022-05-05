import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
    id("dev.projectg.crossplatforms.shadow-conventions")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    api("de.tr7zw:item-nbt-api:2.9.2")
    api(projects.spigotCommon)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("CrossplatForms-SpigotLegacy.jar")
}

// required for pre 1.12 (version issues)
relocate("com.google.common")
relocate("com.google.guava")
// unacceptable version on older spigot
relocate("org.yaml.snakeyaml")
// NBT API used only on legacy spigot
relocate("de.tr7zw.changeme.nbtapi")
relocate("de.tr7zw.annotations")

description = "spigot-legacy"
