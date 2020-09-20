package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScanner
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

open class SonarScanTask : DefaultTask() {
    @Input
    @Optional
    var sonarScannerVersion: String? = null

    @Input
    @Optional
    var sonarScannerPath: String? = null

    @Input
    @Optional
    var serverUrl: String? = null

    @Input
    @Optional
    var serverLogin: String? = null

    @Input
    @Optional
    var serverPassword: String? = null

    @Input
    @Optional
    var serverAuthenticationToken: String? = null

    @InputFile
    lateinit var solutionFile: File

    @Input
    lateinit var projectKey: String

    @Input
    @Optional
    var projectName: String? = null

    @Input
    @Optional
    var projectVersion: String? = null

    @Input
    var configuration: String = "Release"

    @Input
    @Optional
    var platform: String? = null

    @InputFile
    @Optional
    var nunitReport: File? = null

    @InputFile
    @Optional
    var vstestReport: File? = null

    @TaskAction
    fun scan() {
        val sonarScanner = buildSonarScanner()
        when {
            serverAuthenticationToken != null -> {
                logger.debug("Set authentication token to SonarScanner")
                sonarScanner.setCredentials(serverAuthenticationToken!!)
            }
            serverLogin != null && serverPassword != null -> {
                logger.debug("Set login / password to SonarScanner")
                sonarScanner.setCredentials(serverLogin!!, serverPassword!!)
            }
        }

        if (sonarScanner.begin(projectKey, projectName, projectVersion, serverUrl, nunitReport, vstestReport) > 0) {
            throw GradleException("Failed to initialize the scan")
        }

        val msbuild = PluginContext.current.msBuild
        if (msbuild.rebuildSolution(solutionFile.absolutePath, configuration, platform) > 0) {
            throw GradleException("Failed to build solution")
        }

        if (sonarScanner.end() > 0) {
            throw GradleException("Failed to send scan report to server")
        }
    }

    private fun buildSonarScanner(): SonarScanner {
        val builder = PluginContext.current.sonarScannerBuilder
        sonarScannerVersion?.let {
            builder.withVersion(it)
        }
        sonarScannerPath?.let {
            builder.withSonarScannerPath(it)
        }

        return builder.build()
    }
}
