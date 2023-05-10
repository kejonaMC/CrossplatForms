
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.21") // Mirrored from floodgate-spigot - probably the version from 1.8.8
    implementation("de.tr7zw:item-nbt-api:2.11.2")
    api(projects.spigotCommon.common)
}
