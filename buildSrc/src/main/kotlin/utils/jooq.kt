package utils

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration

fun Project.jooqTask(jooqXml: String) {
  val config: Configuration = GenerationTool.load(file(jooqXml).inputStream())
  config.basedir = projectDir.absolutePath

  val taskName = "generateJooq"

  tasks.register(taskName) {
    doLast {
      GenerationTool.generate(config)
    }
  }

  project.the<SourceSetContainer>().getByName("main") {
    java {
      srcDir(config.generator.target.directory)
    }
  }

  tasks.withType<JavaCompile> {
    dependsOn(taskName)
  }
}
