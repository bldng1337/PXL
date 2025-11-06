val lwjglVersion = "3.3.0"
val lwjglNatives = "natives-windows"
val imguiVersion = "1.86.4"

plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // Depend on PXLEng module
    api(project(":PXLEng"))

    // LWJGL BOM for dependency management
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    // LWJGL core modules
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")

    // LWJGL native binaries for Windows
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)

    // JOML - Java OpenGL Math Library
    implementation("org.joml:joml:1.10.3")

    // ImGui Java bindings
    implementation("io.github.spair:imgui-java-binding:$imguiVersion")
    implementation("io.github.spair:imgui-java-lwjgl3:$imguiVersion")
    implementation("io.github.spair:imgui-java-natives-windows:$imguiVersion")
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
        resources {
            srcDir("Libs")
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "ImGui",
            "Implementation-Version" to project.version
        )
    }
}
