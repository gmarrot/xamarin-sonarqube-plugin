package com.betomorrow.gradle.sonarqube.context

import com.betomorrow.gradle.sonarqube.commands.DryRunCommandRunner
import com.betomorrow.gradle.sonarqube.tools.msbuild.DefaultMsBuild
import com.betomorrow.gradle.sonarqube.tools.msbuild.MsBuild
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.DefaultSonarScannerBuilder
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScannerBuilder
import com.betomorrow.xamarin.commands.CommandRunner
import com.betomorrow.xamarin.commands.SystemCommandRunner
import com.betomorrow.xamarin.tools.nuget.Nuget
import com.betomorrow.xamarin.tools.nuget.NugetBuilder
import org.gradle.api.Project

class PluginContext internal constructor(
    val nuget: Nuget,
    val sonarScannerBuilder: SonarScannerBuilder,
    val msBuild: MsBuild
) {

    companion object {

        @JvmStatic
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

            return createApplicationContext(project, commandRunnerInstance)
        }

        private fun realPluginContext(project: Project, verbose: Boolean): PluginContext {
            val commandRunnerInstance = SystemCommandRunner()
            commandRunnerInstance.setVerbose(verbose)

            return createApplicationContext(project, commandRunnerInstance)
        }

        private fun createApplicationContext(project: Project, runner: CommandRunner): PluginContext {
            val nugetInstance = buildNuget(project, runner)
            val sonarScannerBuilderInstance = DefaultSonarScannerBuilder().withCommandBuilder(runner)
            val msBuildInstance = buildMsBuild(project, runner)

            return PluginContext(nugetInstance, sonarScannerBuilderInstance, msBuildInstance)
        }

        private fun buildMsBuild(project: Project, runner: CommandRunner): MsBuild {
            val msBuildPath = project.findProperty("msBuildPath")?.toString()

            return DefaultMsBuild(runner, msBuildPath)
        }

        private fun buildNuget(project: Project, runner: CommandRunner): Nuget {
            val nugetPath = project.findProperty("nugetPath")?.toString()
            val nugetVersion = project.findProperty("nugetVersion")?.toString()

            val nugetBuilder = NugetBuilder().withCommandRunner(runner)
            if (nugetPath != null) {
                nugetBuilder.withNugetPath(nugetPath)
            } else if (nugetVersion != null) {
                nugetBuilder.withVersion(nugetVersion)
            }

            return nugetBuilder.build()
        }

    }

}