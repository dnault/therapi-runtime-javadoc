package com.github.therapi.runtimejavadoc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.javadoc.Javadoc

class RuntimeJavadocPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.afterEvaluate {
            project.task("runtimeJavadoc", type: Javadoc) {
                dependsOn("compileJava")

                ext.timestamp = 0
                ext.timestampFile = project.file("${project.buildDir}/tmp/runtimeJavadoc/timestamp")

                source = { project.sourceSets.main.allJava.findAll { project.file(it).lastModified() > timestamp } }
                classpath = project.configurations.compile + project.files(project.sourceSets.main.output.classesDir)
                destinationDir = project.file("${project.buildDir}/runtimeJavadoc")

                options.doclet = "com.github.therapi.runtimejavadoc.RuntimeJavadocDoclet"
                options.docletpath = project.buildscript.configurations.classpath.files.asType(List)

                doFirst {
                    timestamp = timestampFile.exists() ? timestampFile.lastModified() : 0

                    if (source.isEmpty()) {
                        throw new StopExecutionException("runtime-javadoc is up-to-date")
                    }

                    if (!timestampFile.exists()) {
                        timestampFile.getParentFile().mkdirs()
                        timestampFile.createNewFile()
                    }
                    else {
                        timestampFile.setLastModified(System.currentTimeMillis())
                    }
                }
            }

            project.processResources {
                from project.runtimeJavadoc
            }
        }
    }
}