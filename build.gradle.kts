import com.jfrog.bintray.gradle.*
import com.jfrog.bintray.gradle.tasks.*
import org.apache.maven.model.*
import org.apache.tools.ant.filters.*
import org.gradle.jvm.tasks.*

/*
 * Copyright 2018-2019 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    `java-library`
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

val major = "0"
val minor = "3"
val patch = "2"

group = "club.minnced"
version = "$major.$minor.$patch"

val tokens = mapOf(
        "MAJOR" to major,
        "MINOR" to minor,
        "PATCH" to patch,
        "VERSION" to version
)

repositories {
    jcenter()
}

val powermockVersion = "2.0.4"

dependencies {
    api("org.slf4j:slf4j-api:1.7.25")
    api("com.squareup.okhttp3:okhttp:3.12.0")
    api("org.json:json:20180813")
    implementation("org.jetbrains:annotations:16.0.1")
    
    compileOnly("net.dv8tion:JDA:4.2.0_196")
    compileOnly("com.discord4j:discord4j-core:3.1.0")
    compileOnly("org.javacord:javacord:3.0.6")

    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:3.2.4")
    testImplementation("org.powermock:powermock-module-junit4:$powermockVersion")
    testImplementation("org.powermock:powermock-api-mockito2:$powermockVersion")
    //testCompile("ch.qos.logback:logback-classic:1.2.3")
}

fun org.gradle.api.publish.maven.MavenPom.addDependencies() = withXml {
    asNode().appendNode("dependencies").let { depNode ->
        configurations.api.dependencies.forEach {
            depNode.appendNode("dependency").apply {
                appendNode("groupId", it.group)
                appendNode("artifactId", it.name)
                appendNode("version", it.version)
                appendNode("scope", "compile")
            }
        }
        configurations.implementation.dependencies.forEach {
            depNode.appendNode("dependency").apply {
                appendNode("groupId", it.group)
                appendNode("artifactId", it.name)
                appendNode("version", it.version)
                appendNode("scope", "runtime")
            }
        }
    }
}


fun getProjectProperty(name: String) = project.properties[name] as? String

val javadoc: Javadoc by tasks
val jar: Jar by tasks

val sources = tasks.create("sources", Copy::class.java) {
    from("src/main/java")
    into("$buildDir/sources")
    filter<ReplaceTokens>("tokens" to tokens)
}

javadoc.dependsOn(sources)
javadoc.source = fileTree(sources.destinationDir)
if (!System.getProperty("java.version").startsWith("1.8"))
    (javadoc.options as CoreJavadocOptions).addBooleanOption("html5", true)

val javadocJar = tasks.create("javadocJar", Jar::class.java) {
    dependsOn(javadoc)
    from(javadoc.destinationDir)
    classifier = "javadoc"
}

val sourcesJar = tasks.create("sourcesJar", Jar::class.java) {
    dependsOn(sources)
    from(sources.destinationDir)
    classifier = "sources"
}

publishing {
    publications {
        register("BintrayRelease", MavenPublication::class) {
            from(components["java"])

            artifactId = project.name
            groupId = project.group as String
            version = project.version as String

            artifact(sourcesJar)
            artifact(javadocJar)
        }
    }
}


val bintrayUpload: BintrayUploadTask by tasks
bintrayUpload.apply {
    onlyIf { getProjectProperty("bintrayUsername") != null }
    onlyIf { getProjectProperty("bintrayApiKey") != null }
}

val bintrayPublish: BintrayPublishTask by tasks
bintrayPublish.dependsOn(bintrayUpload)

val compileJava: JavaCompile by tasks
compileJava.options.isIncremental = true
compileJava.source = fileTree(sources.destinationDir)
compileJava.dependsOn(sources)

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val test: Task by tasks
val build: Task by tasks
build.apply {
    dependsOn(javadocJar)
    dependsOn(sourcesJar)
    dependsOn(jar)
    dependsOn(test)
}

bintray {
    user = getProjectProperty("bintrayUsername")
    key = getProjectProperty("bintrayApiKey")
    setPublications("BintrayRelease")
    publish = true

    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = project.name
        vcsUrl = "https://github.com/MinnDevelopment/discord-webhooks.git"
        githubRepo = "MinnDevelopment/discord-webhooks"
        setLicenses("Apache-2.0")
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as String
            vcsTag = project.version as String
            gpg.sign = true
        })
    })
}

