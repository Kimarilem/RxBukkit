import com.jfrog.bintray.gradle.BintrayExtension
import groovy.lang.GroovyObject
import org.gradle.api.publish.maven.MavenPom
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig
import java.net.URL
import java.util.*

group = "com.kimarilem.bukkit.rxbukkit"
version = rootProject.file("VERSION").readText().trim()

plugins {
    id("maven-publish")

    kotlin("jvm") version Versions.kotlin

    id("org.jetbrains.dokka") version Versions.dokka

    id("com.jfrog.bintray") version Versions.bintrayPlugin
    id("com.jfrog.artifactory") version Versions.artifactoryPlugin
}

repositories {
    jcenter()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8", version = Versions.kotlin))
    implementation(Dependencies.rxJava)
    implementation(Dependencies.rxKotlin)

    compileOnly(Dependencies.bukkit)

    testImplementation(kotlin("reflect", version = Versions.kotlin))
    testImplementation(Dependencies.kotlinTest)
    testImplementation(Dependencies.bukkit)
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "1.8"

        apiVersion = "1.3"
        languageVersion = "1.3"
    }
}

tasks.withType<Test> {
    useJUnitPlatform {}
}

tasks.withType<DokkaTask> {
    // TODO This delegateClosureOf can be removed once the Dokka plugin supports the Kotlin DSL
    externalDocumentationLink(delegateClosureOf<DokkaConfiguration.ExternalDocumentationLink.Builder> {
        url = URL("https://hub.spigotmc.org/javadocs/bukkit/")
    })

    outputFormat = "html"
    outputDirectory = "$buildDir/docs/dokka"
}

val javadoc: Javadoc by tasks

val dokkaJavadoc by tasks.creating(DokkaTask::class) {
    outputFormat = "javadoc"
    outputDirectory = javadoc.destinationDir!!.path
}

val jar by tasks.named("jar")

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.destinationDir)
}

artifacts {
    add("archives", jar)
    add("archives", sourcesJar)
    add("archives", javadocJar)
}

fun MavenPom.metadata() {
    name.set("RxBukkit")
    description.set("A ReactiveX wrapper around the Bukkit Event API")
    packaging = "jar"
    url.set("https://github.com/Kimarilem/RxBukkit")

    licenses {
        license {
            name.set("The MIT License (MIT)")
            url.set("http://opensource.org/licenses/MIT")
            distribution.set("repo")
        }
    }
    developers {
        developer {
            id.set("Erackron")
            name.set("Jorai Rijsdijk")
            email.set("jorairijsdijk@gmail.com")
            organization.set("Kimarilem")
        }

        developer {
            id.set("Nauxuron")
            name.set("Wim de With")
            email.set("wf@dewith.io")
            organization.set("Kimarilem")
        }
    }
    scm {
        connection.set("scm:git:git://github.com/Kimarilem/RxBukkit.git")
        developerConnection.set("scm:git:git@github.com:Kimarilem/RxBukkit.git")
        url.set("https://github.com/Kimarilem/RxBukkit")
    }
}

fun MavenPom.addDependencies() = withXml {
    asNode().appendNode("dependencies").let { depNode ->
        configurations.implementation.get().allDependencies.forEach {
            depNode.appendNode("dependency").apply {
                appendNode("groupId", it.group)
                appendNode("artifactId", it.name)
                appendNode("version", it.version)
            }
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("rxbukkit") {
            artifact(jar)
            artifact(sourcesJar)
            artifact(javadocJar)

            groupId = project.group as String?
            artifactId = rootProject.name
            version = project.version as String?

            pom.metadata()
            pom.addDependencies()
        }
    }
}

bintray {
    user = System.getProperty("bintray.user")
    key = System.getProperty("bintray.key")
    setPublications("rxbukkit")

    // TODO This delegateClosureOf can be removed once the bintray plugin supports the Kotlin DSL
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "bukkit"
        name = "RxBukkit"
        userOrg = "kimarilem"
    })
}

// TODO This entire block can be written with the Kotlin DSL once the artifactory plugin supports it
artifactory {
    setContextUrl("http://oss.jfrog.org")

    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", "oss-snapshot-local")
            setProperty("username", System.getProperty("bintray.user"))
            setProperty("password", System.getProperty("bintray.key"))
        })
        defaults(delegateClosureOf<GroovyObject> {
            invokeMethod("publications", "rxbukkit")
            setProperty("publishArtifacts", true)
            setProperty("publishPom", true)
        })
    })
    resolve(delegateClosureOf<ResolverConfig> {
        setProperty("repoKey", "jcenter")
    })
    clientConfig.info.setBuildNumber(System.getProperty("build.number") ?: "manual-" + UUID.randomUUID())
}

val ktlintConfig by configurations.creating

dependencies {
    ktlintConfig(Dependencies.ktlint)
}

val ktlint by tasks.creating(JavaExec::class) {
    group = "verification"
    description = "Check Kotlin code style"

    classpath = ktlintConfig
    main = "com.github.shyiko.ktlint.Main"
    args = listOf("src/**/*.kt", "buildSrc/src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    group = "formatting"
    description = "Fix Kotlin code style deviations"

    classpath = ktlintConfig
    main = "com.github.shyiko.ktlint.Main"
    args = listOf("-F", "src/**/*.kt", "buildSrc/src/**/*.kt")
}

val check by tasks.existing {
    dependsOn(ktlint)
}
