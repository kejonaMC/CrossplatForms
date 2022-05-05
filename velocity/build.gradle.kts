import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
    id("dev.projectg.crossplatforms.shadow-conventions")
}

dependencies {
    //annotationProcessor("com.velocitypowered:velocity-api:3.1.0")
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    api("cloud.commandframework:cloud-velocity:1.6.2")
    api("org.bstats:bstats-velocity:3.0.0")
    api(projects.proxy)
    api(projects.core)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("CrossplatForms-Velocity.jar")
}

relocate("cloud.commandframework")
relocate("org.spongepowered.configurate")
relocate("io.leangen.geantyref") // used by cloud and configurate

exclude("com.google.code.gson")
exclude("com.google.guava")
exclude("com.google.inject")
exclude("net.kyori", "examination", "adventure")
exclude("org.yaml", "snakeyaml")


description = "velocity"
