import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    idea
    jacoco
    java
    id("org.sonarqube") version "6.0.1.5171"
    id("org.springframework.boot") version "3.4.2" apply false
    id("com.diffplug.spotless") version "6.25.0"
}

allprojects {
    group = "ru.hofftech"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

object Version {
    const val JACKSON = "2.18.3"
    const val JSPECIFY = "1.0.0"
    const val JAKARTA_VALIDATION = "3.1.0"
    const val MAP_STRUCT = "1.6.0"
    const val LOMBOK = "1.18.34"
    const val TEST_CONTAINERS = "1.20.1"
    const val SPRING_CLOUD = "2024.0.0"
    const val SPRING_BOOT = "3.4.2"
    const val SPRING_SHELL = "3.3.0"
    const val SPRINGDOC_OPENAPI = "2.8.4"
    const val POSTGRESQL = "42.7.4"
    const val ASSERTJ = "3.24.2"
    const val MOCKITO = "5.19.0"
    const val TELEGRAM_BOT = "1.0.0.rc-1"
    const val SHED_LOCK = "5.16.0"
    const val CAFFEINE = "3.1.8"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "org.sonarqube")

    dependencies {
        implementation(platform("org.springframework.boot:spring-boot-dependencies:${Version.SPRING_BOOT}"))
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:${Version.SPRING_CLOUD}"))
        implementation("org.jspecify:jspecify:${Version.JSPECIFY}")
        implementation("org.mapstruct:mapstruct:${Version.MAP_STRUCT}")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${Version.SPRINGDOC_OPENAPI}")
        implementation("com.fasterxml.jackson.core:jackson-databind:${Version.JACKSON}")
        implementation("com.fasterxml.jackson.core:jackson-annotations:${Version.JACKSON}")
        implementation("com.fasterxml.jackson.core:jackson-core:${Version.JACKSON}")

        annotationProcessor("org.mapstruct:mapstruct-processor:${Version.MAP_STRUCT}")
        annotationProcessor("org.projectlombok:lombok:${Version.LOMBOK}")

        compileOnly("org.projectlombok:lombok:${Version.LOMBOK}")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        testAnnotationProcessor("org.projectlombok:lombok:${Version.LOMBOK}")
        testAnnotationProcessor("org.mapstruct:mapstruct-processor:${Version.MAP_STRUCT}")

        testImplementation(platform("org.testcontainers:testcontainers-bom:${Version.TEST_CONTAINERS}"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.mockito:mockito-core:${Version.MOCKITO}")
        testImplementation("org.assertj:assertj-core:${Version.ASSERTJ}")

    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.displayName()
        }

        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.displayName()
        }

        test {
            useJUnitPlatform()
            testLogging {
                events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            }
            finalizedBy(jacocoTestReport)
        }

        jacocoTestReport {
            dependsOn(test)
            reports {
                xml.required.set(true)
                xml.outputLocation.set(file("${buildDir}/reports/jacoco/test/jacocoTestReport.xml"))
            }

            classDirectories.setFrom(
                files(classDirectories.files.map {
                    fileTree(it) {
                        exclude(
                            "**/stub/**",
                            "**/model/**",
                            "**/dto/**",
                            "**/entity/**",
                            "**/enums/**",
                            "**/config/**",
                            "**/annotation/**",
                            "**/exception/**",
                            "**/repository/**"
                        )
                    }
                })
            )
        }

        jar {
            enabled = false
        }
    }

    sonar {
        properties {
            property("sonar.projectKey", "${System.getenv("PROJECT_NAME_FINAL")}")
            property("sonar.projectName", "${System.getenv("PROJECT_NAME_FINAL")}")
            property("sonar.qualitygate.wait", true)
            property("sonar.coverage.jacoco.xmlReportPath", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        }
    }

    spotless {
        encoding = Charsets.UTF_8
        lineEndings = com.diffplug.spotless.LineEnding.PLATFORM_NATIVE

        format("misc") {
            target("*.gradle", "*.md", ".gitignore")
            indentWithSpaces(4)
            trimTrailingWhitespace()
            endWithNewline()
        }

        java {
            palantirJavaFormat()
            importOrder("", "javax", "java", "\\#")
            indentWithSpaces(4)
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlin {
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlinGradle {
            target("*.gradle.kts")
            indentWithSpaces(4)
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

project(":shared") {
    dependencies {
        implementation("jakarta.validation:jakarta.validation-api:${Version.JAKARTA_VALIDATION}")
    }
}

project(":core") {
    dependencies {
        implementation(project(":shared"))

        // Spring Boot стартеры
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        //Периодический запуск
        implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:${Version.SHED_LOCK}")
        implementation("net.javacrumbs.shedlock:shedlock-spring:${Version.SHED_LOCK}")
        //Кафка
        implementation("org.springframework.cloud:spring-cloud-starter-config")
        implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
        implementation("org.springframework.kafka:spring-kafka")

        // База данных
        implementation("org.flywaydb:flyway-core")
        implementation("org.flywaydb:flyway-database-postgresql")
        runtimeOnly("org.postgresql:postgresql:${Version.POSTGRESQL}")
    }
}

project(":billing") {
    dependencies {
        implementation(project(":shared"))

        // Spring Boot стартеры
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-cache")

        //Кафка
        implementation("org.springframework.cloud:spring-cloud-starter-config")
        implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
        implementation("org.springframework.kafka:spring-kafka")

        // Caffeine Cache
        implementation("com.github.ben-manes.caffeine:caffeine:${Version.CAFFEINE}")

        // База данных
        implementation("org.flywaydb:flyway-core")
        implementation("org.flywaydb:flyway-database-postgresql")
        runtimeOnly("org.postgresql:postgresql:${Version.POSTGRESQL}")
    }
}

project(":telegram") {
    dependencies {
        implementation(project(":shared"))

        // Spring Boot стартеры
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

        // Telegram Bot API
        implementation("io.github.drednote:spring-boot-starter-telegram:${Version.TELEGRAM_BOT}")
    }
}

project(":console") {
    dependencies {
        implementation(project(":shared"))
        implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
        implementation("org.springframework.shell:spring-shell-starter:${Version.SPRING_SHELL}")
    }
}