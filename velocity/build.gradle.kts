import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    //annotationProcessor("com.velocitypowered:velocity-api:3.1.0")
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    api("cloud.commandframework:cloud-velocity:1.7.0")
    api("org.bstats:bstats-velocity:3.0.0")
    api(projects.proxy)
    api(projects.core)
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("cloud.commandframework", "dev.projectg.crossplatforms.shaded.cloud")
            relocate("org.spongepowered.configurate", "dev.projectg.crossplatforms.shaded.configurate")
            // Used by cloud and configurate
            relocate("io.leangen.geantyref", "dev.projectg.crossplatforms.shaded.typetoken")
            relocate("org.bstats", "dev.projectg.crossplatforms.shaded.bstats")
        }
        exclude {
                e -> e.name.startsWith("com.mojang") // all available on velocity
                || e.name.startsWith("org.yaml")
                || e.name.startsWith("com.google")
                || e.name.startsWith("net.kyori")
        }
    }

    archiveFileName.set("CrossplatForms-Velocity.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "velocity"
