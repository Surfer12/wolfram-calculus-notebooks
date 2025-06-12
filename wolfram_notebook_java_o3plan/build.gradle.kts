plugins {
    id("java")
    id("application")
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.flywaydb.flyway") version "10.9.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

repositories { 
    mavenCentral() 
}

dependencies {
    // -- Wolfram J/Link (from your local Wolfram installation) --
    implementation(files("/Applications/Wolfram.app/Contents/SystemFiles/Links/JLink/JLink.jar"))

    // -- Spring Boot Web Layer --
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

    // -- Persistence --
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")        // SQLite for development
    implementation("org.flywaydb:flyway-core")               // Database migrations
    
    // -- JSON Processing --
    implementation("com.fasterxml.jackson.core:jackson-databind")
    
    // -- Logging --
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    
    // -- Testing --
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

application {
    mainClass.set("com.example.notebook.Application")
}

tasks.test { 
    useJUnitPlatform() 
}

// Flyway configuration
flyway {
    url = "jdbc:sqlite:./notebook.db"
    locations = arrayOf("classpath:db/migration")
}
