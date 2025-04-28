plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.samsung"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://www.jetbrains.com/intellij-repository/releases") }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    localPath.set("D:/Program/Android Studio 232")
    plugins.set(listOf("android"))
}

dependencies {
    implementation("com.android.tools.ddms:ddmlib:25.3.0")
    implementation("com.android.tools:sdk-common:25.3.0")
    implementation("org.bytedeco:javacv:1.5.8")
    implementation("org.bytedeco:javacv-platform:1.5.8")
    implementation("org.bytedeco:ffmpeg:5.1.2-1.5.8")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.tinyjee.jgraphx:jgraphx:3.4.1.3")
//    can not run on SRV
//    implementation("org.openjfx:javafx-controls:17")
//    implementation("org.openjfx:javafx-web:17")
//    implementation("org.openjfx:javafx-swing:17")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    runIde {
        ideDir.set(file("D:/Program/Android Studio 232"))
    }

    instrumentCode {
        enabled = false
    }

    instrumentTestCode {
        enabled = false
    }

    buildSearchableOptions{
        enabled = false
    }
}
