package com.betomorrow.xamarin.tools.msbuild

import com.betomorrow.xamarin.commands.CommandRunner
import com.betomorrow.xamarin.commands.SystemCommandRunner
import com.betomorrow.xamarin.tools.xbuild.XBuildCmd

class MsBuild(private val commandRunner: CommandRunner, msBuildPath: String?) {

    private val msBuildPath: String

    constructor(commandRunner: CommandRunner) : this(commandRunner, DEFAULT_MSBUILD_PATH)

    constructor() : this(SystemCommandRunner(), DEFAULT_MSBUILD_PATH)

    init {
        this.msBuildPath = if (msBuildPath.isNullOrEmpty()) DEFAULT_MSBUILD_PATH else msBuildPath
    }

    fun rebuildSolution(solutionPath: String, configuration: String, platform: String? = null): Int {
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