import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    api(project(":spigot-common"))
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("cloud.commandframework", "dev.projectg.crossplatforms.shaded.cloud")
            relocate("me.lucko.commodore", "dev.projectg.crossplatforms.shaded.commodore")
            relocate("net.kyori", "dev.projectg.crossplatforms.shaded.kyori")
            relocate("org.spongepowered.configurate", "dev.projectg.crossplatforms.shaded.configurate")
            // Used by cloud and configurate
            relocate("io.leangen.geantyref", "dev.projectg.crossplatforms.shaded.typetoken")

        }
        exclude {
                e -> e.name.startsWith("com.mojang") // Remove when we support less than 1.13
                || e.name.startsWith("org.yaml") // Available on Spigot
                || e.name.startsWith("com.google")
        }
    }

    println(destinationDirectory.get())
    archiveFileName.set("CrossplatForms-Spigot.jar")
    println(archiveFileName.get())
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "spigot"
