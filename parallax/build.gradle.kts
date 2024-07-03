plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
}

android {
    namespace = "np.com.siddharthaghimire"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.ghimiresiddhartha"
                artifactId = "parallax"
                version = "0.0.1"

                pom {
                    name.set("Parallax")
                    description.set("Try to Parallax designs in Android.")
                    url.set("https://github.com/ghimiresiddhartha/android-library")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("ghimiresiddhartha")
                            name.set("Siddhartha")
                        }
                    }
                    scm {
                        connection.set("scm:git@github.com/ghimiresiddhartha/android-library.git")
                        developerConnection.set("scm:git@github.com/ghimiresiddhartha/android-library.git")
                        url.set("https://github.com/ghimiresiddhartha/android-library")
                    }
                }
            }
        }
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
}