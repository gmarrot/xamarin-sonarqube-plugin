package com.betomorrow.gradle.sonarqube.tools.sonarscanner

import com.betomorrow.xamarin.commands.CommandRunner
import java.io.File

class DefaultSonarScanner(
    private val runner: CommandRunner,
    private val sonarScannerPath: String
) : SonarScanner {
    private var login: String? = null
    private var password: String? = null

    override fun setCredentials(login: String, password: String) {
        this.login = login
        this.password = password
    }

    override fun setCredentials(authenticationToken: String) {
        this.login = authenticationToken
        password = null
    }

    override fun clearCredentials() {
        login = null
        password = null
    }

    override fun begin(
        projectKey: String,
        projectName: String?,
        version: String?,
        url: String?,
        nunitReport: File?,
        vstestReport: File?
    ): Int {
        return execute(
            SonarScannerBeginCmd(
                sonarScannerPath,
                projectKey,
                projectName,
                version,
                url,
                login,
                password,
                nunitReport,
                vstestReport
            )
        )
    }

    override fun end(): Int {
        return execute(
            SonarScannerEndCmd(
                sonarScannerPath,
                login,
                password
            )
        )
    }

    private fun execute(cmd: CommandRunner.Cmd): Int {
        if (!File(sonarScannerPath).exists()) {
            throw Exception("Can't find Sonar Scanner at $sonarScannerPath")
        }

        return runner.run(cmd)
    }
}
