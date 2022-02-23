
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
    version = "0.3.0"
    java.sourceCompatibility = JavaVersion.VERSION_16
    java.targetCompatibility = JavaVersion.VERSION_16

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

subprojects {
    apply(plugin = "maven-publish")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        annotationProcessor("org.projectlombok:lombok:1.18.22")
        compileOnly("org.projectlombok:lombok:1.18.22")
        compileOnly("com.google.code.findbugs:jsr305:3.0.2") // nullability annotations
    }
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
