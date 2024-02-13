plugins {
    java
    id("org.springframework.boot") version "3.1.8" apply(false)
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.ppp"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}


dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5-jakarta")
    implementation("io.hypersistence:hypersistence-utils-hibernate-62:3.7.1")
    implementation("org.springframework.data:spring-data-elasticsearch:5.1.8")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.register("prepareKotlinBuildScriptModel"){}
