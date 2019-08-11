package com.betomorrow.gradle.sonarqube.context

import com.betomorrow.gradle.sonarqube.tools.msbuild.DefaultMsBuild
import com.betomorrow.gradle.sonarqube.tools.msbuild.MsBuild
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.DefaultSonarScannerBuilder
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScannerBuilder
import com.betomorrow.xamarin.commands.CommandRunner
import com.betomorrow.xamarin.tools.nuget.Nuget
import com.betomorrow.xamarin.tools.nuget.NugetBuilder
import org.gradle.api.Project

class DefaultPluginContext(private val project: Project, private val commandRunner: CommandRunner) : PluginContext {

    override val nuget: Nuget by lazy {
        buildNuget(project, commandRunner)
    }

    override val sonarScannerBuilder: SonarScannerBuilder by lazy {
        DefaultSonarScannerBuilder().withCommandBuilder(commandRunner)
    }

    override val msBuild: MsBuild by lazy {
        buildMsBuild(project, commandRunner)
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