plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform("com.github.vhromada.project:project-parent:5.0.0"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(kotlin("test-junit5"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "Result"
    }
}
