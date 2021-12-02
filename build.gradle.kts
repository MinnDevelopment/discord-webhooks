import org.apache.tools.ant.filters.ReplaceTokens
import java.io.ByteArrayOutputStream
import java.time.Duration

plugins {
    `java-library`
    `maven-publish`
    signing

    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

val major = "0"
val minor = "7"
val patch = "3"

group = "club.minnced"
version = "$major.$minor.$patch"

fun getCommit()
    =  System.getenv("GITHUB_SHA")
    ?: System.getenv("GIT_COMMIT")
    ?: try {
        val out = ByteArrayOutputStream()
        exec {
            commandLine("git rev-parse --verify --short HEAD".split(" "))
            standardOutput = out
            workingDir = projectDir
        }
        out.toString("UTF-8").trim()
    } catch (ignored: Throwable) { "N/A" }

val tokens = mapOf(
    "MAJOR" to major,
    "MINOR" to minor,
    "PATCH" to patch,
    "VERSION" to version,
    "COMMIT" to getCommit()
)

repositories {
    mavenCentral()
}

val versions = mapOf(
    "slf4j" to "1.7.32",
    "okhttp" to "3.14.9",
    "json" to "20210307",
    "jda" to "5.0.0-alpha.1",
    "discord4j" to "3.2.1",
    "javacord" to "3.3.2",
    "junit" to "4.13.2",
    "mockito" to "3.6.28", // must be compatible with powermock
    "powermock" to "2.0.9",
    "logback" to "1.2.3",
    "annotations" to "22.0.0"
)

dependencies {
    api("org.slf4j:slf4j-api:${versions["slf4j"]}")
    api("com.squareup.okhttp3:okhttp:${versions["okhttp"]}")
    api("org.json:json:${versions["json"]}")
    implementation("org.jetbrains:annotations:${versions["annotations"]}")

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
    archiveClassifier.set("javadoc")
}

val sourcesJar = tasks.create("sourcesJar", Jar::class.java) {
    dependsOn(sources)
    from(sources.destinationDir)
    archiveClassifier.set("sources")
}

tasks.withType<JavaCompile> {
    val arguments = mutableListOf("-Xlint:deprecation,unchecked,divzero,cast,static,varargs,try")
    options.isIncremental = true
    options.encoding = "UTF-8"
    if (JavaVersion.current().isJava9Compatible) doFirst {
        arguments += "--release"
        arguments += "8"
    }
    doFirst {
        options.compilerArgs = arguments
    }
}

val compileJava: JavaCompile by tasks
compileJava.apply {
    source = fileTree(sources.destinationDir)
    dependsOn(sources)
}

configure<JavaPluginExtension> {
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

            pom.apply(generatePom())
        }
    }
}


// Staging and Promotion

if ("signing.keyId" in properties) {
    signing {
        sign(publishing.publications["Release"])
    }
}

nexusPublishing {
    repositories.sonatype {
        username.set(getProjectProperty("ossrhUser"))
        password.set(getProjectProperty("ossrhPassword"))
        stagingProfileId.set(getProjectProperty("stagingProfileId"))
    }

    // Sonatype is very slow :)
    connectTimeout.set(Duration.ofMinutes(1))
    clientTimeout.set(Duration.ofMinutes(10))

    transitionCheckOptions {
        maxRetries.set(100)
        delayBetween.set(Duration.ofSeconds(5))
    }
}


// To publish run ./gradlew release

val rebuild = tasks.create("rebuild") {
    val clean = tasks.getByName("clean")
    dependsOn(build)
    dependsOn(clean)
    build.mustRunAfter(clean)
}

// Only enable publishing task for properly configured projects
val publishingTasks = tasks.withType<PublishToMavenRepository> {
    enabled = "ossrhUser" in properties
    mustRunAfter(rebuild)
    dependsOn(rebuild)
}

tasks.create("release") {
    dependsOn(publishingTasks)
    afterEvaluate {
        // Collect all the publishing task which upload the archives to nexus staging
        val closeAndReleaseSonatypeStagingRepository: Task by tasks

        // Make sure the close and release happens after uploading
        dependsOn(closeAndReleaseSonatypeStagingRepository)
        closeAndReleaseSonatypeStagingRepository.mustRunAfter(publishingTasks)
    }
}