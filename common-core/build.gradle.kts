plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    val projectParentVersion: String by rootProject.extra
    implementation(platform("com.github.vhromada.project:project-parent:$projectParentVersion"))
    implementation(kotlin("stdlib-jdk8"))
    api(project(":common-result"))
    api("org.springframework.data:spring-data-jpa")
    api("org.springframework.security:spring-security-core")
    api("org.springframework:spring-webmvc")
    api("jakarta.persistence:jakarta.persistence-api")
    api("com.fasterxml.jackson.core:jackson-annotations")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin")
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "Common-core"
    }
}
