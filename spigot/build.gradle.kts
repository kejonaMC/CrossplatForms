import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
    id("dev.projectg.crossplatforms.shadow-conventions")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    api(projects.spigotCommon) {
        // this should be overridden by the version specified here, but just making sure
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("CrossplatForms-Spigot.jar")
}

exclude("org.yaml", "snakeyaml") // available on newer versions of spigot

description = "spigot"
