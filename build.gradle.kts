import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  application

  val kotlinVersion = "1.4.21"
  kotlin("jvm") version kotlinVersion
  kotlin("kapt") version kotlinVersion

  id("it.gianluz.capsule") version "1.0.3"
  id("com.palantir.graal") version "0.7.2"
  id("com.github.ben-manes.versions") version "0.36.0"
}

dependencies {
  // libraries marked as compile instead of implementation so that capsule plugin can resolve them as runtime deps
  compile(kotlin("stdlib-jdk8"))
  compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

  compile("com.squareup.okio:okio:2.9.0")
  compile("com.squareup.okhttp3:okhttp:4.9.0")
  compile("com.squareup.okhttp3:logging-interceptor:4.9.0")

  // keep this version to avoid warning being printed out when running
  compile("com.squareup.retrofit2:retrofit:2.7.2")
  compile("com.squareup.retrofit2:converter-moshi:2.7.2")

  compile("com.squareup.moshi:moshi-adapters:1.11.0")
  compile("com.squareup.moshi:moshi:1.11.0")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")

  compile("com.google.dagger:dagger:2.30.1")
  kapt("com.google.dagger:dagger-compiler:2.30.1")

  compile("com.typesafe:config:1.4.1")
  compile("com.github.ajalt:clikt:2.8.0")

  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-junit"))
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
  testImplementation("org.amshove.kluent:kluent:1.64")
}

application {
  mainClass.set("cash.andrew.hodlr.AppKt")
}

tasks.withType<JavaExec> {
  args = project.properties["project.args"]?.toString()?.split(" ") ?: emptyList()
}

tasks.create<us.kirchmeier.capsule.task.FatCapsule>("createExecutable") {
  group = "Distribution"
  description = "Package into a executable fat jar"

  applicationClass("cash.andrew.hodlr.AppKt")
  reallyExecutable
  archiveFileName.set("hodlr")
}

repositories {
  mavenCentral()
  jcenter()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
  }
}

tasks.withType<KotlinCompile> {
  sourceCompatibility = "1.8"
  targetCompatibility = "1.8"
  kotlinOptions.jvmTarget = "1.8"
}

// still not ready for prime time. Seem like compilation needs to happen for each platform
// currently graal doesn't support ssl
// so this will compile a single "binary" but it will
// still require the JVM to be installed.
graal {
  mainClass("cash.andrew.hodlr.AppKt")
  outputName("hodlr")
  javaVersion("11")
  graalVersion("20.3.0")
  // Graal doesn't like how retrofit includes references to android
  // this would prevent those issues but since we need the JVM fallback
  // this isn't currently an issue.
//  option("-H:+ReportExceptionStackTraces")
//  option("--no-fallback")
//  option("--allow-incomplete-classpath")
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates").configure {
  rejectVersionIf {
    isNonStable(candidate.version)
  }
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}
