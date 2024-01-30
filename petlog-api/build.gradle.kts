plugins {
    java
    id("org.springframework.boot") version "3.1.8"
    id("io.spring.dependency-management") version "1.1.4"
    jacoco
}

group = "com.ppp"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.9"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("org.springframework.data:spring-data-elasticsearch:5.1.8")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation(project(":petlog-domain"))
    implementation(project(":petlog-common"))
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                setIncludes(
                    listOf(
                        "com/ppp/**/controller/*",
                        "com/ppp/**/service/*",
                        "com/ppp/**/util/*",
                        "com/ppp/**/interceptor/*"
                    )
                )
            }
        })
    )
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            isEnabled = true
            element = "CLASS"
            includes = listOf(
                "com.ppp.**.controller.*",
                "com.ppp.**.service.*",
                "com.ppp.**.util.*",
                "com.ppp.**.interceptor.*"
            )

            limit {
                minimum = "0.80".toBigDecimal()
                counter = "LINE"
                value = "COVEREDRATIO"
            }
        }
    }
}

tasks.bootBuildImage {
    builder.set("paketobuildpacks/builder-jammy-base:latest")
}

tasks.register("prepareKotlinBuildScriptModel") {}
