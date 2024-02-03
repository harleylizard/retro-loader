plugins {
    id("java")
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.harleylizard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    implementation("cuchaz:enigma:1.0.0")
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-util:9.5")
    implementation("org.ow2.asm:asm-tree:9.5")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.fabricmc:tiny-remapper:0.8.6")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

gradlePlugin {
    plugins {
        create("retro-loader-gradle") {
            id = "retro-loader-gradle"
            implementationClass = "com.harleylizard.retro.RetroLoaderPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.harleylizard"
            artifactId = "retro-loader-gradle"
            version = "1.0-SNAPSHOT"
        }
    }
    repositories {
        mavenLocal()
    }
}

tasks.test {
    useJUnitPlatform()
}