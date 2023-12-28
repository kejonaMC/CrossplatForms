
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:5.0.47") // see https://www.nathaan.com/explorer/?package=com.mojang&name=authlib
    api(projects.spigotCommon.v114R1)
}
