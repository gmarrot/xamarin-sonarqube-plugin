package com.betomorrow.sonarqube.tools.sonarscanner

import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScannerEndCmd
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class SonarScannerEndCmdTest {

    private lateinit var cmd: SonarScannerEndCmd

    @Test
    fun `test build should return correct command arguments for given scanner path and project key`() {
        // Given
        cmd = SonarScannerEndCmd(SONAR_SCANNER_PATH)

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly("mono", SONAR_SCANNER_PATH, "end")
    }

    @Test
    fun `test build should return correct command arguments when server login has been specified`() {
        // Given
        cmd = SonarScannerEndCmd(SONAR_SCANNER_PATH, "sonarUser")

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "end",
            "/d:sonar.login=sonarUser"
        )
    }

    @Test
    fun `test build should return correct command arguments when server password has been specified`() {
        // Given
        cmd = SonarScannerEndCmd(
            SONAR_SCANNER_PATH,
            password = "sonarPassword"
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "end",
            "/d:sonar.password=sonarPassword"
        )
    }

    @Test
    fun `test build should return correct command arguments when all parameters have been specified`() {
        // Given
        cmd = SonarScannerEndCmd(
            SONAR_SCANNER_PATH,
            "sonarUser",
            "sonarPassword"
        )

        // When
        val cmdArguments = cmd.build()

        // Then
        Assertions.assertThat(cmdArguments).containsExactly(
            "mono",
            SONAR_SCANNER_PATH,
            "end",
            "/d:sonar.login=sonarUser",
            "/d:sonar.password=sonarPassword"
        )
    }

    companion object {
        const val SONAR_SCANNER_PATH = "/test/SonarScanner.MsBuild.exe"
    }

}