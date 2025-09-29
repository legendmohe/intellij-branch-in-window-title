import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  java
  kotlin("jvm") version "2.2.0"
  id("org.jetbrains.intellij.platform") version "2.9.0"
}

group = "org.jetbrains"

version = "2.0.1"

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
  compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.7.2"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.mockito:mockito-core:2.21.0")
  testImplementation("org.assertj:assertj-core:3.24.0")

  intellijPlatform {
    intellijIdeaUltimate("2025.2")
    bundledPlugins("Git4Idea")
    pluginVerifier()
    zipSigner()
  }
}

intellijPlatform {
  buildSearchableOptions = false
  instrumentCode = true

  pluginConfiguration {
    version = project.version.toString()

    ideaVersion {
      sinceBuild = "252"
      untilBuild = provider { null }
    }
  }

  signing {
    certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
    privateKey = providers.environmentVariable("PRIVATE_KEY")
    password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
  }

  publishing {
    token = providers.environmentVariable("PUBLISH_TOKEN")
  }

  pluginVerification {
    ides {
      recommended()
    }
  }
}

tasks.withType(JavaCompile::class.java) {
  options.isDeprecation = true
  options.encoding = "UTF-8"
}

tasks {
  test {
    useJUnitPlatform()
  }
}
