plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    val kvisionVersion: String by System.getProperties()
    id("io.kvision") version kvisionVersion

    // for publishing
    id("signing")
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

version = "1.0.5"
group = "com.bittokazi.sonartype"

val resourcesVersion = "1.0.5"

repositories {
    mavenCentral()
    mavenLocal()
}

// Versions
val kotlinVersion: String by System.getProperties()
val kvisionVersion: String by System.getProperties()

kotlin {
    js(IR) {
        browser {
            useEsModules()
            commonWebpackConfig {
                outputFileName = "main.bundle.js"
                sourceMaps = false
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
        compilerOptions {
            target.set("es2015")
        }
    }
    sourceSets["jsMain"].dependencies {
        implementation("io.kvision:kvision:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
        implementation("io.kvision:kvision-datetime:$kvisionVersion")
        implementation("io.kvision:kvision-richtext:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-upload:$kvisionVersion")
        implementation("io.kvision:kvision-imask:$kvisionVersion")
        implementation("io.kvision:kvision-toastify:$kvisionVersion")
        implementation("io.kvision:kvision-fontawesome:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-icons:$kvisionVersion")
        implementation("io.kvision:kvision-i18n:$kvisionVersion")
        implementation("io.kvision:kvision-pace:$kvisionVersion")
        implementation("io.kvision:kvision-handlebars:$kvisionVersion")
        implementation("io.kvision:kvision-maps:$kvisionVersion")
        implementation("io.kvision:kvision-rest:$kvisionVersion")
        implementation("io.kvision:kvision-jquery:$kvisionVersion")
        implementation("io.kvision:kvision-routing-navigo-ng:$kvisionVersion")
        implementation("io.kvision:kvision-state:$kvisionVersion")
        implementation("io.kvision:kvision-state-flow:$kvisionVersion")
        implementation("io.kvision:kvision-select-remote:$kvisionVersion")
    }
    sourceSets["jsTest"].dependencies {
        implementation(kotlin("test-js"))
        implementation("io.kvision:kvision-testutils:$kvisionVersion")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest>().configureEach {
    enabled = false
}

val npmPackageDir = layout.buildDirectory.dir("kotlin-kvision-spa-framework-resources")

tasks.register<Copy>("prepareNpmPackage") {
    from("src/jsMain/resources") // static files: index.html, css/, images/
    into(npmPackageDir)
}

tasks.register("npmPackage") {
    dependsOn("prepareNpmPackage")
    doLast {
        val packageJsonFile = npmPackageDir.get().file("package.json").asFile
        packageJsonFile.writeText("""
            {
              "name": "kotlin-kvision-spa-framework-resources",
              "version": "${resourcesVersion}",
              "main": "index.js",
              "files": [
                "index.js",
                "index.html",
                "static/**"
              ]
            }
        """.trimIndent())
        println("Local NPM package prepared in: ${npmPackageDir.get().asFile.absolutePath}")
    }
}

publishing {
    publications {
        named<MavenPublication>("js") {
            pom {
                name.set("Kvision Single Page Application Framework")
                description.set("A Kotlin/JS SPA framework built with KVision")
                url.set("https://github.com/bittokazi/kotlin-kvision-spa-framework")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("bittokazi")
                        name.set("Bittokazi")
                        email.set("bitto.kazi@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/bittokazi/kotlin-kvision-spa-framework.git")
                    developerConnection.set("scm:git:ssh://github.com/bittokazi/kotlin-kvision-spa-framework.git")
                    url.set("https://github.com/bittokazi/kotlin-kvision-spa-framework")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(System.getenv("CENTRAL_PUBLISHER_USERNAME"))
            password.set(System.getenv("CENTRAL_PUBLISHER_PASSWORD"))
        }
    }
}

val signingKey: String? = System.getenv("SIGNING_KEY")

signing {
    useInMemoryPgpKeys(signingKey, "")
    sign(publishing.publications["js"])
}

