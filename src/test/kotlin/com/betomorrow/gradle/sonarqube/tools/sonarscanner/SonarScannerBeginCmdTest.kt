package com.betomorrow.gradle.sonarqube.tools.sonarscanner

import org.assertj.core.api.Assertions
import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.junit.jupiter.api.Test

class SonarScannerBeginCmdTest {
    private lateinit var cmd: SonarScannerBeginCmd

    @Rule
    val testProjectDir = TemporaryFolder()

    @Test
    fun `test build should return correct command arguments for given scanner path and project key`() {
        // Given
        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly("mono",
            SONAR_SCANNER_PATH, "begin", "/k:$PROJECT_KEY")
    }

    @Test
    fun `test build should return correct command arguments when project name has been specified`() {
        // Given
        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY,
            "Project Name"
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "begin",
            "/k:$PROJECT_KEY",
            "/n:Project Name"
        )
    }

    @Test
    fun `test build should return correct command arguments when project version has been specified`() {
        // Given
        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY,
            version = "1.0"
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "begin",
            "/k:$PROJECT_KEY",
            "/v:1.0"
        )
    }

    @Test
    fun `test build should return correct command arguments when server url has been specified`() {
        // Given
        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY,
            url = "https://sonarqube.example.com"
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "begin",
            "/k:$PROJECT_KEY",
            "/d:sonar.host.url=https://sonarqube.example.com"
        )
    }

    @Test
    fun `test build should return correct command arguments when server login has been specified`() {
        // Given
        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY,
            login = "sonarUser"
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "begin",
            "/k:$PROJECT_KEY",
            "/d:sonar.login=sonarUser"
        )
    }

    @Test
    fun `test build should return correct command arguments when server password has been specified`() {
        // Given
        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY,
            password = "sonarPassword"
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "begin",
            "/k:$PROJECT_KEY",
            "/d:sonar.password=sonarPassword"
        )
    }

    @Test
    fun `test build should return correct command arguments when NUnit report has been specified`() {
        // Given
        testProjectDir.create()
        val nunitReport = testProjectDir.newFile("nunit-report.xml")

        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY,
            nunitReport = nunitReport
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "begin",
            "/k:$PROJECT_KEY",
            "/d:sonar.cs.nunit.reportsPaths=${nunitReport.absolutePath}"
        )
    }

    @Test
    fun `test build should return correct command arguments when VSTest report has been specified`() {
        // Given
        testProjectDir.create()
        val vstestReport = testProjectDir.newFile("vstest-report.xml")

        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY,
            vstestReport = vstestReport
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "begin",
            "/k:$PROJECT_KEY",
            "/d:sonar.cs.vstest.reportsPaths=${vstestReport.absolutePath}"
        )
    }

    @Test
    fun `test build should return correct command arguments when all parameters have been specified`() {
        // Given
        testProjectDir.create()
        val nunitReport = testProjectDir.newFile("nunit-report.xml")
        val vstestReport = testProjectDir.newFile("vstest-report.xml")

        cmd = SonarScannerBeginCmd(
            SONAR_SCANNER_PATH,
            PROJECT_KEY,
            "Project Name",
            "1.0",
            "https://sonarqube.example.com",
            "sonarUser",
            "sonarPassword",
            nunitReport,
            vstestReport
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "begin",
            "/k:$PROJECT_KEY",
            "/n:Project Name",
            "/v:1.0",
            "/d:sonar.host.url=https://sonarqube.example.com",
            "/d:sonar.login=sonarUser",
            "/d:sonar.password=sonarPassword",
            "/d:sonar.cs.nunit.reportsPaths=${nunitReport.absolutePath}",
            "/d:sonar.cs.vstest.reportsPaths=${vstestReport.absolutePath}"
        )
    }

    companion object {
        const val SONAR_SCANNER_PATH = "/test/SonarScanner.MsBuild.exe"
        const val PROJECT_KEY = "my-project-key"
    }
}