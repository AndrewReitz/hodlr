import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import us.kirchmeier.capsule.manifest.CapsuleManifest
import us.kirchmeier.capsule.task.FatCapsule

plugins {
  application

  val kotlinVersion = "1.3.50"
  kotlin("jvm") version kotlinVersion
  kotlin("kapt") version kotlinVersion

  id("it.gianluz.capsule") version "1.0.3"
  id("com.palantir.graal") version "0.6.0"
  id("com.github.ben-manes.versions") version "0.27.0"
  id("com.gradle.build-scan") version "3.0"
}

buildScan {
  termsOfServiceUrl = "https://gradle.com/terms-of-service"
  termsOfServiceAgree = "yes"

  publishAlways()
}

dependencies {
  // libraries marked as compile so that capsule plugin can resolve them as runtime deps
  compile(kotlin("stdlib-jdk8"))
  compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

  compile("com.squareup.okio:okio:2.2.2")
  compile("com.squareup.okhttp3:okhttp:4.2.2")
  compile("com.squareup.okhttp3:logging-interceptor:4.2.2")
  compile("com.squareup.retrofit2:retrofit:2.6.2")
  compile("com.squareup.retrofit2:converter-moshi:2.6.2")

  compile("com.squareup.moshi:moshi-adapters:1.9.1")
  compile("com.squareup.moshi:moshi:1.9.1")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.1")

  compile("com.google.dagger:dagger:2.25.2")
  kapt("com.google.dagger:dagger-compiler:2.25.2")

  compile("com.typesafe:config:1.3.4")
  compile("com.github.ajalt:clikt:2.2.0")

  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-junit"))
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.2")
  testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
  testImplementation("org.amshove.kluent:kluent:1.56")
}

application {
  mainClassName = "cash.andrew.hodlr.AppKt"
}

tasks.withType<JavaExec> {
  args = project.properties["project.args"]?.toString()?.split(" ") ?: emptyList()
}

tasks.create<FatCapsule>("createExecutable") {
  group = "Distribution"
  description = "Package into a executable fat jar"

  applicationClass("cash.andrew.hodlr.AppKt")
  reallyExecutable
  archiveFileName.set("hodlr")
}

allprojects {
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
}

// still not ready for prime time yet
graal {
  mainClass("cash.andrew.hodlr.AppKt")
  outputName("hodlr")
}
