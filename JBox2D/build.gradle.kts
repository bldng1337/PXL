plugins {
    `java-library`
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "JBox2D",
            "Implementation-Version" to project.version
        )
    }
}
