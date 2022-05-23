import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    api("de.tr7zw:item-nbt-api:2.9.2")
    api(projects.spigotCommon)
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("com.google.inject", "dev.projectg.crossplatforms.shaded.guice")
            // older versions of Spigot have a Guava that is too old for the used Guice
            relocate("com.google.common", "dev.projectg.crossplatforms.shaded.google.common") // i.e. Guava
            relocate("cloud.commandframework", "dev.projectg.crossplatforms.shaded.cloud")
            relocate("me.lucko.commodore", "dev.projectg.crossplatforms.shaded.commodore")
            relocate("net.kyori", "dev.projectg.crossplatforms.shaded.kyori")
            relocate("org.spongepowered.configurate", "dev.projectg.crossplatforms.shaded.configurate")
            relocate("io.leangen.geantyref", "dev.projectg.crossplatforms.shaded.typetoken")
            relocate("org.bstats", "dev.projectg.crossplatforms.shaded.bstats")
            // Only used on spigot-legacy - legacy versions don't contain a version acceptable for configurate
            relocate("org.yaml.snakeyaml", "dev.projectg.crossplatforms.shaded.snakeyaml")
            relocate("de.tr7zw.changeme.nbtapi", "dev.projectg.crossplatforms.shaded.nbtapi")
            relocate("de.tr7zw.annotations", "dev.projectg.crossplatforms.shaded.tr7zw.annotations")
        }
        exclude {
                e ->
            val name = e.name
            (name.startsWith("com.google")
            && !name.startsWith("com.google.inject")
            && !name.startsWith("com.google.guava")) // contains package name of com.google.common
            || name.startsWith("javax.inject")
        }
    }

    archiveFileName.set("CrossplatForms-SpigotLegacy.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "spigot-legacy"
