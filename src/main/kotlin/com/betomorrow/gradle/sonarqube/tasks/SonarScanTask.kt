package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.xamarin.tools.sonarqube.SonarScanner
import com.betomorrow.xamarin.tools.sonarqube.SonarScannerBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

open class SonarScanTask : DefaultTask() {

    private val commandRunner = PluginContext.current.commandRunner
    private val msbuild = PluginContext.current.msBuild

    @Input
    @Optional
    var sonarScannerVersion: String? = null

    @Input
    @Optional
    var sonarScannerPath: String? = null

    @Optional
    var serverLogin: String? = null

    @Optional
    var serverPassword: String? = null

    @Optional
    var serverAuthenticationToken: String? = null

    lateinit var solutionFile: File

    @Input
    var configuration: String = "Release"

    @Input
    @Optional
    var platform: String? = null

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
        serverAuthenticationToken?.let { token ->
            logger.debug("Set authentication token to sonar scanner")
            sonarScanner.setCredentials(token)
        }

        println("Solution: $solutionFile")
        println("Sonar scan!")
    }

    private fun buildSonarScanner(): SonarScanner {
        val sonarScannerBuilder = SonarScannerBuilder()
            .withCommandBuilder(commandRunner)
        sonarScannerVersion?.let {
            sonarScannerBuilder.withVersion(it)
        }
        sonarScannerPath?.let {
            sonarScannerBuilder.withSonarScannerPath(it)
        }

        return sonarScannerBuilder.build()
    }

    private fun buildSolution() {
        msbuild.rebuildSolution(solutionFile.absolutePath, configuration, platform)
    }

}