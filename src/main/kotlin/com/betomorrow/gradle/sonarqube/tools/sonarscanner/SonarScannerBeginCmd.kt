package com.betomorrow.gradle.sonarqube.tools.sonarscanner

import com.betomorrow.xamarin.commands.CommandRunner
import java.io.File

class SonarScannerBeginCmd(
    private val sonarScannerPath: String,
    private val projectKey: String,
    private val projectName: String? = null,
    private val version: String? = null,
    private val url: String? = null,
    private val login: String? = null,
    private val password: String? = null,
    private val nunitReport: File? = null
) : CommandRunner.Cmd {
    override fun build(): MutableList<String> {
        val cmd = mutableListOf("mono", sonarScannerPath, "begin", "/k:$projectKey")

        if (!projectName.isNullOrBlank()) {
            cmd.add("/n:$projectName")
        }

        if (!version.isNullOrBlank()) {
            cmd.add("/v:$version")
        }

        if (!url.isNullOrBlank()) {
            cmd.add("/d:sonar.host.url=$url")
        }

        if (!login.isNullOrBlank()) {
            cmd.add("/d:sonar.login=$login")
        }

        if (!password.isNullOrBlank()) {
            cmd.add("/d:sonar.password=$password")
        }

        if (nunitReport != null) {
            cmd.add("/d:sonar.cs.nunit.reportsPaths=${nunitReport.absolutePath}")
        }

        return cmd
    }
}