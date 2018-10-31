plugins {
    `java-library`
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.1"
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

group = "club.minnced"
version = "0.1.0"

repositories {
    jcenter()
}

dependencies {
    api("org.slf4j:slf4j-api:1.7.25")
    api("com.squareup.okhttp3:okhttp:3.11.0")
    api("org.json:json:20160810")
    implementation("org.jetbrains:annotations:16.0.1")
    testCompile("junit:junit:4.12")
    testCompile("ch.qos.logback:logback-classic:1.2.3")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}