plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(platform("com.github.vhromada.project:project-parent:5.0.0"))
    implementation(kotlin("stdlib-jdk8"))
    api(project(":common-core"))
    api("io.github.microutils:kotlin-logging")
    api("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    api(kotlin("test-junit5"))
    api("org.mockito:mockito-junit-jupiter")
    api("com.nhaarman.mockitokotlin2:mockito-kotlin")
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "Test"
    }
}
