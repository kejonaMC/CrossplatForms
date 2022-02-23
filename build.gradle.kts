import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

allprojects{
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = "dev.projectg"
    version = "0.2.0"
    java.sourceCompatibility = JavaVersion.VERSION_16
    java.targetCompatibility = JavaVersion.VERSION_16

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

repositories {
    apply(plugin = "maven-publish")

    mavenLocal()
    mavenCentral()

    maven("https://libraries.minecraft.net/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.22")
    compileOnly("org.projectlombok:lombok:1.18.22")
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    compileOnly("org.geysermc:core:2.0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.1.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    api("cloud.commandframework:cloud-paper:1.6.1")
    api("cloud.commandframework:cloud-minecraft-extras:1.6.1")
    api("me.lucko:commodore:1.9")
    api("net.kyori:adventure-platform-bukkit:4.0.1")
    api("org.spongepowered:configurate-yaml:4.1.2")
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
                e -> e.name.startsWith("com.mojang.brigadier") // Remove when we support less than 1.13
                || e.name.startsWith("org.yaml") // Available on Spigot
                || e.name.startsWith("com.google")
        }
    }

    println(destinationDirectory.get())
    archiveFileName.set("CrossplatForms.jar")
    println(archiveFileName.get())
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

// todo: process resources / token replacement

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
