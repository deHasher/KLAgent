plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven("https://repository.ow2.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation("org.javassist", "javassist", "3.30.2-GA"   )
    implementation("org.ow2.asm",   "asm",       "9.6-SNAPSHOT")
    implementation("org.ow2.asm",   "asm-tree",  "9.6-SNAPSHOT")
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("klagent.jar")

        manifest.attributes(
            "Premain-Class"           to "ru.dehasher.klagent.KLAgent",
            "Can-Retransform-Classes" to "true",
            "Can-Redefine-Classes"    to "true",
        )

        relocate("org.objectweb.asm", "ru.dehasher.klagent.asm")
        relocate("org.javassist",     "ru.dehasher.klagent.javassist")

//        destinationDirectory.set(file("C:\\Users\\deHasher\\AppData\\Roaming\\.minecraft\\libraries\\ru\\klauncher\\klagent"))
//        destinationDirectory.set(file("/Users/dehasher/Library/Application Support/minecraft/libraries/ru/klauncher/klagent"))
    }

    build {
        dependsOn(shadowJar)
    }
}