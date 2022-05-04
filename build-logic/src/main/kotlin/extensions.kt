import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.specs.Spec

val excludedDependencies = mutableMapOf<String, MutableSet<Spec<ResolvedDependency>>>()
val relocatedPackages = mutableMapOf<String, MutableSet<String>>()

/**
 * Exclude an artifact based off group and artifact name(s)
 */
fun Project.exclude(group: String, vararg names: String) {
    names.forEach { name ->
        excludedDependencies.getOrPut(project.name) {
            mutableSetOf(Spec {d -> d.moduleGroup == group && d.moduleName == name})
        }
    }
}

/**
 * Exclude artifact(s) solely based off group
 */
fun Project.exclude(group: String) {
    excludedDependencies.getOrPut(project.name) {
        mutableSetOf(Spec {d -> d.moduleGroup == group})
    }
}

fun Project.exclude(spec: Spec<ResolvedDependency>) {
    excludedDependencies.getOrPut(project.name) {
        mutableSetOf(spec)
    }
}

fun Project.relocate(pattern: String) {
    relocatedPackages.getOrPut(project.name) { mutableSetOf() }
            .add(pattern)
}