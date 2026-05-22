plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "admin-starter"
include(":frontend")
include(":proto:imports")
include(":proto:api")
project(":proto:api").projectDir = file("proto/service")
include(":service")
