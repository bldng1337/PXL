val imguiVersion = "1.86.4"
val lwjglVersion = "3.3.0"

plugins {
    `java-library`
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Depend on other modules
    api(project(":PXLEng"))
    api(project(":JBox2D"))
    api(project(":ImGui"))

    // LWJGL BOM for dependency management
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    // LWJGL core modules
    implementation("org.lwjgl", "lwjgl")

    // JOML - Java OpenGL Math Library
    implementation("org.joml:joml:1.10.3")

    // ImGui Java bindings
    implementation("io.github.spair:imgui-java-binding:$imguiVersion")
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
    }
}

application {
    mainClass.set("me.pxl.editor.EditorMain")
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "PXL Editor",
            "Implementation-Version" to project.version,
            "Main-Class" to "me.pxl.editor.EditorMain"
        )
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    workingDir = projectDir
}
