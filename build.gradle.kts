import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `maven-publish`
  `java-library`
  signing
  jacoco
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  kotlin("jvm") version "1.7.10"
  id("org.jetbrains.dokka") version "1.7.10"
}

group = "com.fortechteams"
version = project
  .rootProject
  .file("version.txt")
  .readText(Charsets.UTF_8)
  .trim()

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {

  api("org.keycloak", "keycloak-admin-client", "18.0.2")

  implementation("org.slf4j", "slf4j-api", "1.7.36")

  testImplementation("io.mockk", "mockk", "1.9.3")
  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
  testImplementation("ch.qos.logback:logback-classic:1.4.1")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
  withSourcesJar()
  withJavadocJar()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["kotlin"])
      artifact(tasks["sourcesJar"])
      artifact(tasks["javadocJar"])
      pom {
        name.set(project.name)
        description.set("KCloak library - A Kotlin DSL for KeyCloak")
        url.set("https://oss.4techteams.com/kcloak")
        licenses {
          license {
            name.set("MIT")
            url.set("https://opensource.org/licenses/MIT")
          }
        }
        organization {
          name.set("4TechTeams.com")
          url.set("https://www.4techteams.com")
        }
        developers {
          developer {
            id.set("frne")
            name.set("Frank Neff")
            url.set("https://frankneff.com")
          }
        }
        scm {
          url.set(
            "https://github.com/4TechTeams/kcloak.git"
          )
          connection.set(
            "scm:git:git://github.com/4TechTeams/kcloak.git"
          )
          developerConnection.set(
            "scm:git:git://github.com/4TechTeams/kcloak.git"
          )
        }
        issueManagement {
          url.set("https://github.com/4TechTeams/kcloak/issues")
        }
      }
    }
  }
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
      val ossrhUsername = providers
        .environmentVariable("OSSRH_USERNAME")
        .forUseAtConfigurationTime()
      val ossrhPassword = providers
        .environmentVariable("OSSRH_PASSWORD")
        .forUseAtConfigurationTime()
      if (ossrhUsername.isPresent && ossrhPassword.isPresent) {
        username.set(ossrhUsername.get())
        password.set(ossrhPassword.get())
      }
    }
  }
}

signing {
  val signingKey = providers
    .environmentVariable("GPG_SIGNING_KEY")
    .forUseAtConfigurationTime()
  val signingPassphrase = providers
    .environmentVariable("GPG_SIGNING_PASSPHRASE")
    .forUseAtConfigurationTime()
  if (signingKey.isPresent && signingPassphrase.isPresent) {
    useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
    val extension = extensions
      .getByName("publishing") as PublishingExtension
    sign(extension.publications)
  }
}

jacoco {
  toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
  dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
    events = setOf(
      TestLogEvent.STARTED,
      TestLogEvent.SKIPPED,
      TestLogEvent.PASSED,
      TestLogEvent.FAILED,
      TestLogEvent.STANDARD_OUT,
      TestLogEvent.STANDARD_ERROR
    )
  }
  finalizedBy(tasks.jacocoTestReport)
}
