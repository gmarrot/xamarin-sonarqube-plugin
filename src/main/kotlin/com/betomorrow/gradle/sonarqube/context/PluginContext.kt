package com.betomorrow.gradle.sonarqube.context

import com.betomorrow.xamarin.commands.CommandRunner
import com.betomorrow.xamarin.commands.FakeCommandRunner
import com.betomorrow.xamarin.commands.SystemCommandRunner
import com.betomorrow.xamarin.tools.msbuild.MsBuild
import com.betomorrow.xamarin.tools.nuget.Nuget
import com.betomorrow.xamarin.tools.nuget.NugetBuilder
import org.gradle.api.Project

class PluginContext {

    companion object {

        @JvmStatic
        lateinit var current: ApplicationContext
            private set

        @JvmStatic
        fun configure(project: Project) {
            val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() == true
            val verbose = project.findProperty("verbose")?.toString()?.toBoolean() == true
            current = if (dryRun) fakeApplicationContext(project, verbose) else realApplicationContext(project, verbose)
        }

        private fun fakeApplicationContext(project: Project, verbose: Boolean): ApplicationContext {
            val commandRunnerInstance = FakeCommandRunner()
            commandRunnerInstance.setVerbose(verbose)

            return createApplicationContext(project, commandRunnerInstance)
        }

        private fun realApplicationContext(project: Project, verbose: Boolean): ApplicationContext {
            val commandRunnerInstance = SystemCommandRunner()
            commandRunnerInstance.setVerbose(verbose)

            return createApplicationContext(project, commandRunnerInstance)
        }

        private fun createApplicationContext(project: Project, runner: CommandRunner): ApplicationContext {
            val nugetInstance = buildNuget(project, runner)
            val msBuildInstance = buildMsBuild(project, runner)

            return object : ApplicationContext {
                override val commandRunner: CommandRunner
                    get() = runner

                override val msBuild: MsBuild
                    get() = msBuildInstance

                override val nuget: Nuget
                    get() = nugetInstance
            }
        }

        private fun buildMsBuild(project: Project, runner: CommandRunner): MsBuild {
            val msBuildPath = project.findProperty("msBuildPath")?.toString()

            return MsBuild(runner, msBuildPath)
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

    interface ApplicationContext {
        val commandRunner: CommandRunner
        val msBuild: MsBuild
        val nuget: Nuget
    }

}