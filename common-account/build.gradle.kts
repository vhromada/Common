plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(platform("com.github.vhromada.project:project-parent:5.0.1"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    api(project(":common-core"))
    api("org.hibernate:hibernate-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin")
    testImplementation("com.h2database:h2")
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "Account"
    }
}
