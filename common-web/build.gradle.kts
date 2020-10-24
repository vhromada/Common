plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(platform("com.github.vhromada.project:project-parent:5.0.1"))
    implementation(kotlin("stdlib-jdk8"))
    api(project(":common-result"))
    api("org.springframework:spring-webmvc")
    api("com.fasterxml.jackson.core:jackson-annotations")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(kotlin("test-junit5"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "Web"
    }
}
