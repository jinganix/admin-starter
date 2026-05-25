import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.remove
import utils.Props
import utils.Vers
import utils.jooqTask
import utils.Vers.versionAuthorizationServer
import utils.Vers.versionJooq
import utils.Vers.versionCaffeine
import utils.Vers.versionCommonsCodec
import utils.Vers.versionCommonsLang3
import utils.Vers.versionFlyway
import utils.Vers.versionGuava
import utils.Vers.versionJackson
import utils.Vers.versionJacksonAnnotations
import utils.Vers.versionJwt
import utils.Vers.versionLombok
import utils.Vers.versionMapstruct
import utils.Vers.versionMysqlConnector
import utils.Vers.versionNetty
import utils.Vers.versionPeashooter
import utils.Vers.versionProtobufJava
import utils.Vers.versionRedisson
import utils.Vers.versionTestContainers
import utils.Vers.versionWebpb

plugins {
  id("com.diffplug.spotless")
  id("com.github.kt3k.coveralls")
  id("com.google.protobuf")
  id("conventions.common")
  id("io.spring.dependency-management")
  id("org.springframework.boot")
  jacoco
  java
}

group = "io.github.jinganix.admin.starter"
version = "${versionWebpb}-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs.add("-Xlint:-processing")
}

dependencies {
  annotationProcessor("io.github.jinganix.webpb:webpb-processor:${versionWebpb}")
  annotationProcessor("org.projectlombok:lombok:$versionLombok")
  annotationProcessor("org.mapstruct:mapstruct-processor:${versionMapstruct}")
  compileOnly("org.projectlombok:lombok:$versionLombok")
  developmentOnly("org.jooq:jooq-codegen:${versionJooq}")
  developmentOnly("org.jooq:jooq-meta-extensions:${versionJooq}")
  implementation("com.auth0:java-jwt:${versionJwt}")
  implementation("com.fasterxml.jackson.core:jackson-annotations:${versionJacksonAnnotations}")
  implementation("com.github.ben-manes.caffeine:caffeine:${versionCaffeine}")
  implementation("com.google.guava:guava:${versionGuava}")
  implementation("com.google.protobuf:protobuf-java:${versionProtobufJava}")
  implementation("commons-codec:commons-codec:${versionCommonsCodec}")
  implementation("io.github.jinganix.peashooter:peashooter:${versionPeashooter}")
  implementation("io.github.jinganix.webpb:webpb-proto:${versionWebpb}")
  implementation("io.github.jinganix.webpb:webpb-runtime:${versionWebpb}")
  implementation("io.github.jinganix.webpb:webpb-commons:${versionWebpb}")
  implementation("io.netty:netty-resolver-dns-native-macos:${versionNetty}:osx-aarch_64")
  implementation("org.apache.commons:commons-lang3:${versionCommonsLang3}")
  implementation("org.flywaydb:flyway-core:${versionFlyway}")
  implementation("org.flywaydb:flyway-mysql:${versionFlyway}")
  implementation("org.mapstruct:mapstruct:${versionMapstruct}")
  implementation("org.redisson:redisson:${versionRedisson}")
  implementation("org.redisson:redisson-spring-cache:${versionRedisson}")
  implementation("org.springframework.boot:spring-boot-configuration-processor")
  implementation("org.aspectj:aspectjweaver")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.boot:spring-boot-starter-jooq")
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.data:spring-data-commons")
  implementation("org.springframework.security:spring-security-oauth2-authorization-server:${versionAuthorizationServer}")
  implementation("tools.jackson.core:jackson-core:${versionJackson}")
  implementation("tools.jackson.core:jackson-databind:${versionJackson}")
  protobuf(project(":proto:imports"))
  protobuf(project(":proto:admin"))
  runtimeOnly("com.mysql:mysql-connector-j:${versionMysqlConnector}")
  testAnnotationProcessor("org.mapstruct:mapstruct-processor:${versionMapstruct}")
  testAnnotationProcessor("org.projectlombok:lombok:$versionLombok")
  testCompileOnly("org.projectlombok:lombok:$versionLombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.testcontainers:testcontainers-junit-jupiter:${versionTestContainers}")
  testImplementation("org.testcontainers:testcontainers-mysql:${versionTestContainers}")
  testImplementation("org.testcontainers:testcontainers:${versionTestContainers}")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.bootJar {
  archiveFileName.set("admin-starter-service.jar")
}

jooqTask("jooq.xml")

val jacocoClassDirs =
  sourceSets.main.get().output.asFileTree.matching {
    exclude("io/github/jinganix/admin/starter/proto/**")
    exclude("io/github/jinganix/admin/starter/generated/**")
    exclude("io/github/jinganix/admin/starter/schema/**")
    exclude("io/github/jinganix/admin/starter/tests/schema/**")
    exclude("**/*MapperImpl.class")
  }

tasks.jacocoTestReport {
  enabled = true
  dependsOn(tasks.test)
  classDirectories.setFrom(jacocoClassDirs)
  reports {
    xml.required.set(true)
    html.required.set(true)
  }
}

tasks.jacocoTestCoverageVerification {
  enabled = Props.verifyCoverage
  dependsOn(tasks.jacocoTestReport)
  classDirectories.setFrom(jacocoClassDirs)
  violationRules {
    rule {
      limit {
        minimum = BigDecimal.valueOf(Props.jacocoMinCoverage)
      }
    }
  }
}

tasks.check {
  dependsOn(tasks.jacocoTestReport)
  dependsOn(tasks.jacocoTestCoverageVerification)
}

jacoco {
  toolVersion = Vers.versionJacocoAgent
}

if (Props.verifyJavaDocs) {
  java {
    withJavadocJar()
    withSourcesJar()
  }
}

val versionGoogleJavaFormat: String by project
spotless {
  java {
    googleJavaFormat(versionGoogleJavaFormat)
    targetExclude("build/**/*.java")
  }
}

tasks.check {
  dependsOn(tasks.spotlessCheck)
}

coveralls {
  jacocoReportPath = "build/reports/jacoco/test/jacocoTestReport.xml"
}

tasks.coveralls {
  dependsOn(tasks.jacocoTestReport)
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:${Vers.versionProtobuf}"
  }
  plugins {
    id("webpb") {
      artifact = "io.github.jinganix.webpb:webpb-protoc-java:${versionWebpb}:all@jar"
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
        id("webpb")
      }
    }
  }
}
