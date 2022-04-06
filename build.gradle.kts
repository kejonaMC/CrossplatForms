
plugins {
    java
    `java-library`
    `maven-publish`
    id("net.kyori.indra.git")
}

allprojects{
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "net.kyori.indra.git")

    group = "dev.projectg"
    version = "1.1.0"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(8)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        // Disable creating of test report files
        reports.html.required.set(false)
        reports.junitXml.required.set(false)
    }

    tasks.named("build") {
        dependsOn(tasks.named<Test>("test"))
    }

    // Ensure platform jars are flagged as multi release
    tasks.jar {
        manifest {
            attributes("Multi-Release" to "true")
        }
    }

    tasks.processResources {
        expand(
            "project_description" to "Bedrock Edition forms, inventory menus, and more.",
            "project_url" to "https://github.com/ProjectG-Plugins/CrossplatForms",
            "project_version" to project.version,
            "git_branch" to (indraGit.branchName() ?: "UNKNOWN"),
            "git_commit" to (indraGit.commit()?.abbreviate(7)?.name() ?: "UNKNOWN"),
            "build_number" to (System.getenv("BUILD_NUMBER") ?: "UNKNOWN")
        )
    }
}

subprojects {
    dependencies {
        testAnnotationProcessor("org.projectlombok:lombok:1.18.22")
        testCompileOnly("org.projectlombok:lombok:1.18.22")
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")

        annotationProcessor("org.projectlombok:lombok:1.18.22")
        compileOnly("org.projectlombok:lombok:1.18.22")
        compileOnly("com.google.code.findbugs:jsr305:3.0.2") // nullability annotations
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
