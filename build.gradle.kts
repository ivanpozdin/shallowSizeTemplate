import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

val jvmTargetVersion: String by project

plugins {
    kotlin("jvm")
    java
    id("io.gitlab.arturbosch.detekt").version("1.22.0-RC2")
}

group = "spbu.kotlin.class.shallowSize"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    apply {
        plugin("kotlin")
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = jvmTargetVersion
            languageVersion = "1.5"
            apiVersion = "1.5"
        }
    }
}