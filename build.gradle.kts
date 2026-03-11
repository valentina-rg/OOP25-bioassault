plugins {
    java
    application
}

group = "it.unibo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configurazione per il punto P9: mainClass è dove parte il programma
application {
    mainClass.set("it.unibo.bioassault.Main")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// Configurazione per il FAT-JAR (Punto P8/P9)
tasks.jar {
    manifest {
        attributes["Main-Class"] = "it.unibo.bioassault.Main"
    }
    // Include tutte le dipendenze in un unico file
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}