plugins {
    `java-library`
    `maven-publish`
}

group = "com.harleylizard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven( "https://maven.fabricmc.net/")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    api(files("libraries/client.jar"))

    api("net.minecraft:launchwrapper:1.5")
    api("com.google.code.gson:gson:2.10.1")
    api("com.google.guava:guava:32.1.3-jre")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.harleylizard"
            artifactId = "retro-loader"
            version = "1.0-SNAPSHOT"
        }
    }
    repositories {
        mavenLocal()
    }
}