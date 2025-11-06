val lwjglVersion = "3.3.0"
val lwjglNatives = "natives-windows"

plugins {
    `java-library`
}

dependencies {
    // LWJGL BOM for dependency management
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    // LWJGL core modules
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-lz4")
    implementation("org.lwjgl", "lwjgl-nuklear")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-shaderc")
    implementation("org.lwjgl", "lwjgl-spvc")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl-yoga")

    // LWJGL native binaries for Windows
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-lz4", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-spvc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-yoga", classifier = lwjglNatives)

    // JOML - Java OpenGL Math Library
    implementation("org.joml:joml:1.10.3")

    // Gson for JSON serialization
    implementation("com.google.code.gson:gson:2.9.0")

    implementation(project(":JBox2D"))
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
            "Implementation-Title" to "PXLEng",
            "Implementation-Version" to project.version
        )
    }
}
