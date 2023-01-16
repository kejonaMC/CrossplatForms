
dependencies {
    testImplementation("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    api(projects.spigotCommon.common)
    implementation(projects.spigotCommon.v114R1)
}