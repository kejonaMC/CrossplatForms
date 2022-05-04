import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// Borrowed from Floodgate

plugins {
    id("com.github.johnrengelman.shadow")
}

tasks {
    val shadowJar = named<ShadowJar>("shadowJar") {

        val sJar: ShadowJar = this
        doFirst {
            providedDependencies[project.name]?.forEach { string ->
                sJar.dependencies {
                    println("Excluding $string from ${project.name}")
                    exclude(dependency(string))
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

val providedDependencies = mutableMapOf<String, MutableSet<String>>()
val relocatedPackages = mutableMapOf<String, MutableSet<String>>()

fun Project.provided(pattern: String, name: String, version: String, excludedOn: Int = 0b110) {
    providedDependencies.getOrPut(project.name) { mutableSetOf() }
            .add("${calcExclusion(pattern, 0b100, excludedOn)}:" +
                    "${calcExclusion(name, 0b10, excludedOn)}:" +
                    calcExclusion(version, 0b1, excludedOn))
    dependencies.add("compileOnlyApi", "$pattern:$name:$version")
}

fun Project.provided(dependency: ProjectDependency) =
        provided(dependency.group!!, dependency.name, dependency.version!!)


fun Project.relocate(pattern: String) =
        relocatedPackages.getOrPut(project.name) { mutableSetOf() }
                .add(pattern)

fun calcExclusion(section: String, bit: Int, excludedOn: Int): String =
        if (excludedOn and bit > 0) section else ""