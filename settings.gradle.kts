plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "admin-starter"
include(":frontend:admin")
include(":proto:imports")
include(":proto:service")
include(":service:admin-starter")
