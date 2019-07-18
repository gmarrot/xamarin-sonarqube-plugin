package com.betomorrow.gradle.sonarqube.tools.msbuild

import com.betomorrow.xamarin.commands.CommandRunner
import com.betomorrow.xamarin.commands.SystemCommandRunner
import com.betomorrow.xamarin.tools.xbuild.XBuildCmd

class DefaultMsBuild(private val commandRunner: CommandRunner, msBuildPath: String?) : MsBuild {

    private val msBuildPath: String

    constructor(commandRunner: CommandRunner) : this(commandRunner, DEFAULT_MSBUILD_PATH)

    constructor() : this(SystemCommandRunner(), DEFAULT_MSBUILD_PATH)

    init {
        this.msBuildPath = if (msBuildPath.isNullOrEmpty()) DEFAULT_MSBUILD_PATH else msBuildPath
    }

    override fun rebuildSolution(solutionPath: String, configuration: String, platform: String?): Int {
        val cmd = XBuildCmd(msBuildPath)
        cmd.configuration = configuration
        if (!platform.isNullOrEmpty()) {
            cmd.addProperty("Platform", platform)
        }
        cmd.target = "Rebuild"
        cmd.projectPath = solutionPath

        return commandRunner.run(cmd)
    }

    companion object {
        const val DEFAULT_MSBUILD_PATH = "/Library/Frameworks/Mono.framework/Commands/msbuild"
    }

}