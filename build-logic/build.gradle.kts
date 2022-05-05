import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    `kotlin-dsl`
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17" // good to match target of compileJava task
    }
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.com.github.johnrengelman", "shadow", "7.1.2")
}
