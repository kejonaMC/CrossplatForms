# How to build

Executing `./gradlew clean build` will execute typical building, as well as tests and shading required for each platform.  
Once finished the build for a given platform is available in `<platform>/build/libs`. `Crossplatforms-<platform>.jar` is the built plugin that
can run a server. `<platform>-<version>.jar` contains only native sources.

# Contribution guidelines

- Follow general best practices.
- Follow the existing indentation and space styling.
- The only nullability annotation used should be from javax. You can configure IntelliJ IDEA to use javax annotations when automatically nullability annotations.
- Avoid using the static getters on `Crossplatforms.class` or any of the platform main classes, unless the other options aren't feasible.
- Keep breaking changes to configuration classes as small as possible, but don't let it hold better design back.
- If you make any breaking changes to configuration classes, please write a version translator to automatically update older versions to the newer one. Configurate has
examples of this as well as the existing version translators that we have.
