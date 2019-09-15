package com.betomorrow.gradle.sonarqube

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.gradle.sonarqube.extensions.SONARQUBE_EXTENSION_NAME
import com.betomorrow.gradle.sonarqube.extensions.SonarQubePluginExtension
import com.betomorrow.gradle.sonarqube.tasks.NugetRestoreTask
import com.betomorrow.gradle.sonarqube.tasks.SonarScanTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class SonarQubePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create(SONARQUBE_EXTENSION_NAME, SonarQubePluginExtension::class.java, target)

            afterEvaluate { project ->
                PluginContext.configure(project)

                val sonarqubeExtension = extensions.getByType(SonarQubePluginExtension::class.java)

                val nugetRestoreTask = if (tasks.findByName(NUGET_RESTORE_TASK_NAME) != null) {
                    val t = tasks.replace(NUGET_RESTORE_TASK_NAME, NugetRestoreTask::class.java)
                    t.group = "Build"
                    t.description = "Restores NuGet packages."

                    t
                } else {
                    tasks.register(NUGET_RESTORE_TASK_NAME, NugetRestoreTask::class.java) { t ->
                        t.group = "Build"
                        t.description = "Restores NuGet packages."
                    }.get()
                }

                val solutionFile = findSolutionFile(project)
                val projectVersion = getProjectVersion(project)

                tasks.register("sonarScan", SonarScanTask::class.java) { t ->
                    t.group = SONARQUBE_GROUP
                    t.description = "Scans the current project and uploads the report to SonarQube server."

                    t.serverUrl = sonarqubeExtension.url
                    t.serverLogin = sonarqubeExtension.login
                    t.serverPassword = sonarqubeExtension.password
                    t.serverAuthenticationToken = sonarqubeExtension.authenticationToken

                    t.solutionFile = solutionFile
                    t.projectKey = sonarqubeExtension.projectKey
                    t.projectName = sonarqubeExtension.projectName
                    t.projectVersion = projectVersion
                    t.configuration = sonarqubeExtension.configuration
                    t.platform = sonarqubeExtension.platform
                    t.nunitReport = sonarqubeExtension.nunitReportFile

                    t.dependsOn(nugetRestoreTask)
                }
            }
        }
    }

    private fun findSolutionFile(project: Project): File {
        return File(project.projectDir.absolutePath)
            .walkTopDown()
            .first { file -> file.isFile && file.extension == "sln" }
    }

    private fun getProjectVersion(project: Project): String? {
        val version = project.version.toString()
        if (version == "unspecified") {
            return null
        }

        return version
    }

    companion object {
        const val SONARQUBE_GROUP = "SonarQube"

        private const val NUGET_RESTORE_TASK_NAME = "nugetRestore"
    }
}