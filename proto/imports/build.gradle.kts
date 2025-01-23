import utils.Vers.versionWebpb

plugins {
  id("conventions.common")
  java
}

repositories {
  mavenCentral()
}

dependencies {
  compileOnly("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
}
