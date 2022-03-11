import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {
    annotationProcessor("com.velocitypowered:velocity-api:3.1.0")
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    api("cloud.commandframework:cloud-velocity:1.6.2")
    api(project(":core"))
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("cloud.commandframework", "dev.projectg.crossplatforms.shaded.cloud")
            relocate("org.spongepowered.configurate", "dev.projectg.crossplatforms.shaded.configurate")
            // Used by cloud and configurate
            relocate("io.leangen.geantyref", "dev.projectg.crossplatforms.shaded.typetoken")

        }
        exclude {
                e -> e.name.startsWith("com.mojang") // all available on velocity
                || e.name.startsWith("org.yaml.snakeyaml")
                || e.name.startsWith("com.google")
                || e.name.startsWith("net.kyori.adventure")
                || e.name.startsWith("net.kyori.examination")
        }
    }

    archiveFileName.set("CrossplatForms-Velocity.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "velocity"