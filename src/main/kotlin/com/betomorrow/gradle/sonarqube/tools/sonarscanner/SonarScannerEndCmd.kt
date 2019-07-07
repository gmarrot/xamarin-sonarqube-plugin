package com.betomorrow.gradle.sonarqube.tools.sonarscanner

import com.betomorrow.xamarin.commands.CommandRunner

class SonarScannerEndCmd(
    private val sonarScannerPath: String,
    private val login: String? = null,
    private val password: String? = null
) : CommandRunner.Cmd {

    override fun build(): MutableList<String> {
        val cmd = mutableListOf("mono", sonarScannerPath, "end")

        if (!login.isNullOrBlank()) {
            cmd.add("/d:sonar.login=$login")
        }

        if (!password.isNullOrBlank()) {
            cmd.add("/d:sonar.password=$password")
        }

        return cmd
    }

}