import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// Borrowed from Floodgate

plugins {
    id("com.github.johnrengelman.shadow")
}

tasks {
    val shadowJar = named<ShadowJar>("shadowJar") {

        val sJar: ShadowJar = this
        doFirst {
            excludedDependencies[project.name]?.forEach { spec ->
                sJar.dependencies {
                    exclude(spec)
                }
            }

            // relocations made in included project dependencies are for whatever reason not
            // forwarded to the project implementing the dependency.
            // (e.g. a relocation in `core` will relocate for core. But when you include `core` in
            // for example Velocity, the relocation will be gone for Velocity)
            addRelocations(project, sJar)
        }
    }
    named("build") {
        dependsOn(shadowJar)
    }
}

fun addRelocations(project: Project, shadowJar: ShadowJar) {
    callAddRelocations(project.configurations.getByName("api"), shadowJar)
    callAddRelocations(project.configurations.getByName("implementation"), shadowJar)

    relocatedPackages[project.name]?.forEach { pattern ->
        println("Relocating $pattern for ${shadowJar.project.name}")
        shadowJar.relocate(pattern, "dev.projectg.crossplatforms.shaded.$pattern")
    }
}

fun callAddRelocations(configuration: Configuration, shadowJar: ShadowJar) =
        configuration.dependencies.forEach {
            if (it is ProjectDependency)
                addRelocations(it.dependencyProject, shadowJar)
        }
