package com.betomorrow.gradle.sonarqube.context

import com.betomorrow.gradle.sonarqube.commands.DryRunCommandRunner
import com.betomorrow.gradle.sonarqube.tools.msbuild.MsBuild
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScannerBuilder
import com.betomorrow.xamarin.commands.SystemCommandRunner
import com.betomorrow.xamarin.tools.nuget.Nuget
import org.gradle.api.Project

interface PluginContext {
    val nuget: Nuget
    val sonarScannerBuilder: SonarScannerBuilder
    val msBuild: MsBuild

    companion object {
        lateinit var current: PluginContext
            internal set

        @JvmStatic
        fun configure(project: Project) {
            val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() == true
            val verbose = project.findProperty("verbose")?.toString()?.toBoolean() == true
            current = if (dryRun) fakePluginContext(project, verbose) else realPluginContext(project, verbose)
        }

        private fun fakePluginContext(project: Project, verbose: Boolean): PluginContext {
            val commandRunnerInstance = DryRunCommandRunner()
            commandRunnerInstance.setVerbose(verbose)

            return DefaultPluginContext(project, commandRunnerInstance)
        }

        private fun realPluginContext(project: Project, verbose: Boolean): PluginContext {
            val commandRunnerInstance = SystemCommandRunner()
            commandRunnerInstance.setVerbose(verbose)

            return DefaultPluginContext(project, commandRunnerInstance)
        }
    }
}
