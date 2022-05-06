import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    api(project(":spigot-common")) {
        // this should be overridden by the version specified here, but just making sure
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("com.google.inject", "dev.projectg.crossplatforms.shaded.guice")
            relocate("cloud.commandframework", "dev.projectg.crossplatforms.shaded.cloud")
            relocate("me.lucko.commodore", "dev.projectg.crossplatforms.shaded.commodore")
            relocate("net.kyori", "dev.projectg.crossplatforms.shaded.kyori")
            relocate("org.spongepowered.configurate", "dev.projectg.crossplatforms.shaded.configurate")
            relocate("io.leangen.geantyref", "dev.projectg.crossplatforms.shaded.typetoken")
            relocate("org.bstats", "dev.projectg.crossplatforms.shaded.bstats")
        }
        exclude {
                e ->
            val name = e.name
            name.startsWith("org.yaml") // Available on Spigot
            || (name.startsWith("com.google") && !name.startsWith("com.google.inject"))
            || name.startsWith("javax.inject")
        }
    }

    archiveFileName.set("CrossplatForms-Spigot.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "spigot"
