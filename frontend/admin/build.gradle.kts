import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.remove
import utils.Vers.versionProtobuf
import utils.Vers.versionWebpb

plugins {
  id("com.google.protobuf")
  id("conventions.common")
  id("org.gradle.java")
}

dependencies {
  implementation("io.github.jinganix.webpb:webpb-proto:$versionWebpb")
  protobuf(project(":proto:imports"))
  protobuf(project(":proto:service"))
}

tasks.clean {
  enabled = false
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:$versionProtobuf"
  }
  plugins {
    id("ts") {
      artifact = "io.github.jinganix.webpb:webpb-protoc-ts:$versionWebpb:all@jar"
    }
  }
  generateProtoTasks {
    ofSourceSet("main").forEach {
      it.doFirst {
        delete(it.outputBaseDir)
      }
      it.builtins {
        remove("java")
      }
      it.plugins {
        id("ts")
      }
    }
  }
}
