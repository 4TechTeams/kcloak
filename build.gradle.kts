import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
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
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
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
}
