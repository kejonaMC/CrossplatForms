import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// Borrowed from Floodgate

plugins {
    id("com.github.johnrengelman.shadow")
}

tasks {
    val shadowJarProvider = named<ShadowJar>("shadowJar") {

        val shadowJar: ShadowJar = this
        doFirst {
            val specs = excludedDependencies[project.name]
            if (specs?.isEmpty() != false) {
                println("No exclusions for ${project.name}")
            } else {
                specs.forEach { spec ->
                    shadowJar.dependencies {
                        exclude {
                            val excluding = spec.isSatisfiedBy(it)
                            if (excluding) {
                                println("Excluding ${it.moduleGroup}:${it.moduleName} from ${project.name}")
                            } else {
                                println("Not excluding ${it.moduleGroup}:${it.moduleName} from ${project.name}")
                            }
                            return@exclude excluding
                        }
                    }
                }
            }

            // relocations made in included project dependencies are for whatever reason not
            // forwarded to the project implementing the dependency.
            // (e.g. a relocation in `core` will relocate for core. But when you include `core` in
            // for example Velocity, the relocation will be gone for Velocity)
            addRelocations(project, shadowJar)
        }
        doLast {
            excludedDependencies[project.name]?.clear()
            relocatedPackages[project.name]?.clear()
        }
    }
    named("build") {
        dependsOn(shadowJarProvider)
    }
}

fun addRelocations(project: Project, shadowJar: ShadowJar) {
    callAddRelocations(project.configurations.getByName("api"), shadowJar)
    callAddRelocations(project.configurations.getByName("implementation"), shadowJar)

    // sorting is only done so that the packages are listed alphabetically
    val relocations = relocatedPackages[project.name]
    if (relocations?.isEmpty() != false) {
        println("No relocations for ${project.name}")
    } else {
        relocations.sorted().forEach { pattern ->
            println("Relocating $pattern for ${shadowJar.project.name}")
            shadowJar.relocate(pattern, "dev.projectg.crossplatforms.shaded.$pattern")
        }
    }
}

fun callAddRelocations(configuration: Configuration, shadowJar: ShadowJar) =
        configuration.dependencies.forEach {
            if (it is ProjectDependency)
                addRelocations(it.dependencyProject, shadowJar)
        }
