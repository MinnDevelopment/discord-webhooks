import de.marcphilipp.gradle.nexus.NexusPublishExtension
import io.codearte.gradle.nexus.BaseStagingTask
import io.codearte.gradle.nexus.NexusStagingExtension
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.publish.maven.MavenPom
import java.time.Duration

plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.codearte.nexus-staging") version "0.22.0"
    id("de.marcphilipp.nexus-publish") version "0.4.0"
}


val major = "0"
val minor = "5"
val patch = "5"

group = "club.minnced"
version = "$major.$minor.$patch"

val tokens = mapOf(
    "MAJOR" to major,
    "MINOR" to minor,
    "PATCH" to patch,
    "VERSION" to version
)

repositories {
    mavenCentral()
    jcenter() // Legacy :(
}

val versions = mapOf(
    "slf4j" to "1.7.25",
    "okhttp" to "3.14.9",
    "json" to "20180813",
    "jda" to "4.2.0_196",
    "discord4j" to "3.1.0",
    "javacord" to "3.0.6",
    "junit" to "4.12",
    "mockito" to "3.6.28",
    "powermock" to "2.0.9",
    "logback" to "1.2.3"
)

dependencies {
    api("org.slf4j:slf4j-api:${versions["slf4j"]}")
    api("com.squareup.okhttp3:okhttp:${versions["okhttp"]}")
    api("org.json:json:${versions["json"]}")
    implementation("org.jetbrains:annotations:16.0.1")

    compileOnly("net.dv8tion:JDA:${versions["jda"]}")
    compileOnly("com.discord4j:discord4j-core:${versions["discord4j"]}")
    compileOnly("org.javacord:javacord:${versions["javacord"]}")

    testImplementation("junit:junit:${versions["junit"]}")
    testImplementation("org.mockito:mockito-core:${versions["mockito"]}")
    testImplementation("org.powermock:powermock-module-junit4:${versions["powermock"]}")
    testImplementation("org.powermock:powermock-api-mockito2:${versions["powermock"]}")
    testImplementation("net.dv8tion:JDA:${versions["jda"]}")
    testImplementation("com.discord4j:discord4j-core:${versions["discord4j"]}")
    testImplementation("org.javacord:javacord:${versions["javacord"]}")
    //testCompile("ch.qos.logback:logback-classic:${versions["logback"]}")
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


// Signing


val signJar = tasks.create("signJar", Sign::class.java) {
    dependsOn(jar)
    sign(jar)
}

val signJavadocJar = tasks.create("signJavadocJar", Sign::class.java) {
    dependsOn(javadocJar)
    sign(javadocJar)
}

val signSourcesJar = tasks.create("signSourcesJar", Sign::class.java) {
    dependsOn(sourcesJar)
    sign(sourcesJar)
}

val signPom = tasks.create("signPom", Sign::class.java) {
    val pom = file("${buildDir}/publications/Release/pom-default.xml")
    sign(pom)
}

val signModule = tasks.create("signModule", Sign::class.java) {
    val module = file("${buildDir}/publications/Release/module.json")
    sign(module)
}

val signFiles = tasks.create("signFiles") {
    dependsOn(signJar, signJavadocJar, signSourcesJar, signPom, signModule)
}

// Generate pom file for maven central

fun generatePom(): MavenPom.() -> Unit {
    return {
        packaging = "jar"
        name.set(project.name)
        description.set("Provides easy to use bindings for the Discord Webhook API")
        url.set("https://github.com/MinnDevelopment/discord-webhooks")
        scm {
            url.set("https://github.com/MinnDevelopment/discord-webhooks")
            connection.set("scm:git:git://github.com/MinnDevelopment/discord-webhooks")
            developerConnection.set("scm:git:ssh:git@github.com:MinnDevelopment/discord-webhooks")
        }
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("Minn")
                name.set("Florian Spieß")
                email.set("business@minnced.club")
            }
        }
    }
}


// Publish

publishing {
    publications {
        register("Release", MavenPublication::class) {
            from(components["java"])

            artifactId = project.name
            groupId = project.group as String
            version = project.version as String

            artifact(sourcesJar)
            artifact(javadocJar)
            artifact(signJar.signatureFiles.first()) {
                classifier = null
                extension = "jar.asc"
            }
            artifact(signJavadocJar.signatureFiles.first()) {
                classifier = "javadoc"
                extension = "jar.asc"
            }
            artifact(signSourcesJar.signatureFiles.first()) {
                classifier = "sources"
                extension = "jar.asc"
            }
            artifact(signPom.signatureFiles.first()) {
                classifier = null
                extension = "pom.asc"
            }
            artifact(signModule.signatureFiles.first()) {
                classifier = null
                extension = "module.asc"
            }

            pom.apply(generatePom())
        }
    }
}



// Prepare for publish

val generateMetadataFileForReleasePublication: Task by tasks
signModule.dependsOn(generateMetadataFileForReleasePublication)
signModule.mustRunAfter(generateMetadataFileForReleasePublication)

val generatePomFileForReleasePublication: GenerateMavenPom by tasks
signPom.dependsOn(generatePomFileForReleasePublication)
signPom.mustRunAfter(generatePomFileForReleasePublication)

tasks.withType(AbstractPublishToMaven::class.java) {
    dependsOn(signFiles)
    mustRunAfter(signFiles)
}

// Staging and Promotion

configure<NexusStagingExtension> {
    username = getProjectProperty("ossrhUser")
    password = getProjectProperty("ossrhPassword")
    stagingProfileId = getProjectProperty("stagingProfileId")
}

configure<NexusPublishExtension> {
    nexusPublishing {
        repositories.sonatype {
            username.set(getProjectProperty("ossrhUser"))
            password.set(getProjectProperty("ossrhPassword"))
            stagingProfileId.set(getProjectProperty("stagingProfileId"))
        }
        // Sonatype is very slow :)
        connectTimeout.set(Duration.ofMinutes(1))
        clientTimeout.set(Duration.ofMinutes(10))
    }
}

// This links the close/release tasks to the right repository (from the publication above)
val publishToSonatype: Task by tasks
tasks.withType<BaseStagingTask> {
    dependsOn(publishToSonatype)
    mustRunAfter(publishToSonatype)
    // We give each step half an hour because it takes very long sometimes ...
    numberOfRetries = 30
    delayBetweenRetriesInMillis = 60000
}
