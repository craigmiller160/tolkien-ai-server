import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    val kotlinVersion = "2.0.0-Beta5"

    id("org.springframework.boot") version "3.2.4"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("io.craigmiller160.gradle.defaults") version "1.2.2"
    id("com.diffplug.spotless") version "6.17.0"
    id("io.spring.dependency-management") version "1.1.4"
}

java {
    sourceCompatibility = JavaVersion.VERSION_20
}

dependencies {
    compileOnly("org.slf4j:slf4j-api:2.0.13")
    compileOnly("jakarta.annotation:jakarta.annotation-api")
    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework.data:spring-data-mongodb")
    compileOnly("io.weaviate:client:4.6.0") {
        exclude("commons-logging", "commons-logging")
    }
}

kotlin {
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_20)
    }
}

configure<SpotlessExtension> {
    kotlin {
        ktfmt()
    }
}