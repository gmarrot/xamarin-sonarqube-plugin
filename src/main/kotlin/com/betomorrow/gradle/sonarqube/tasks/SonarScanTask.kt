package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScanner
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScannerBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

open class SonarScanTask : DefaultTask() {

    private val sonarScannerBuilder = PluginContext.current.sonarScannerBuilder
    private val msbuild = PluginContext.current.msBuild

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

    @Input
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

        if (sonarScanner.begin(projectKey, projectName, projectVersion, serverUrl, nunitReport) > 0) {
            throw GradleException("Failed to initialize the scan")
        }

        if (msbuild.rebuildSolution(solutionFile.absolutePath, configuration, platform) > 0) {
            throw GradleException("Failed to build solution")
        }

        if (sonarScanner.end() > 0) {
            throw GradleException("Failed to send scan report to server")
        }
    }

    private fun buildSonarScanner(): SonarScanner {
        sonarScannerVersion?.let {
            sonarScannerBuilder.withVersion(it)
        }
        sonarScannerPath?.let {
            sonarScannerBuilder.withSonarScannerPath(it)
        }

        return sonarScannerBuilder.build()
    }

}