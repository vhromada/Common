plugins {
    kotlin("jvm")
}

dependencies {
    val projectParentVersion: String by rootProject.extra
    implementation(platform("com.github.vhromada.project:project-parent:$projectParentVersion"))
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
