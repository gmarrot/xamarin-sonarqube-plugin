package com.betomorrow.gradle.sonarqube.commands

import com.betomorrow.xamarin.commands.CommandRunner

class DryRunCommandRunner : CommandRunner {

    private val mutableExecutedCommands = mutableListOf<CommandRunner.Cmd>()
    private var verbose = false

    val executedCommands: List<CommandRunner.Cmd>
        get() = mutableExecutedCommands

    override fun setVerbose(verbose: Boolean) {
        this.verbose = verbose
    }

    override fun run(cmd: CommandRunner.Cmd?): Int {
        if (cmd == null) {
            return 1
        }

        if (verbose) {
            println("Execute command : ${cmd.build().joinToString(" ")}")
        }

        mutableExecutedCommands.add(cmd)
        return 0
    }

}