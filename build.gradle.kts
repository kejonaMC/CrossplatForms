plugins {
    java
    `maven-publish`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://libraries.minecraft.net/")
    }

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.opencollab.dev/main/")
    }

    maven {
        name = "opencollab-release-repo"
        url = uri("https://repo.opencollab.dev/maven-releases/")
    }

    maven {
        name = "opencollab-snapshot-repo"
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("cloud.commandframework:cloud-paper:1.6.1")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.6.1")
    implementation("me.lucko:commodore:1.9")
    implementation("net.kyori:adventure-platform-bukkit:4.0.1")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    compileOnly("org.geysermc:core:2.0.0-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.1.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.projectlombok:lombok:1.18.22")
}

group = "dev.projectg"
version = "0.1.0"
description = "CrossplatForms"
java.sourceCompatibility = JavaVersion.VERSION_16

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
