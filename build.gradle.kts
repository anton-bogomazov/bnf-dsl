plugins {
    kotlin("jvm") version "2.2.21"
    `maven-publish`
}

group = "com.abogomazov.bnf"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("bnf-engine")
}