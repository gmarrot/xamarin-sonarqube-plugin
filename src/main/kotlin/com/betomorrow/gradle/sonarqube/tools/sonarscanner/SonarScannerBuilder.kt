package com.betomorrow.gradle.sonarqube.tools.sonarscanner

import com.betomorrow.groovy.extensions.UrlExtension
import com.betomorrow.xamarin.commands.CommandRunner
import com.betomorrow.xamarin.commands.DefaultCommandRunner
import com.betomorrow.xamarin.files.FileDownloader
import com.betomorrow.xamarin.files.ZippedFile
import java.io.File
import java.net.URL
import java.nio.file.Paths

class SonarScannerBuilder {

    private lateinit var runner: CommandRunner

    private lateinit var sonarScannerVersion: String
    private lateinit var sonarScannerPath: String

    private val nugetCacheDir: String
        get() {
            return Paths.get(System.getProperty("user.home"))
                .resolve(".nuget")
                .resolve("caches")
                .toAbsolutePath()
                .toString()
        }

    fun withCommandBuilder(runner: CommandRunner): SonarScannerBuilder {
        this.runner = runner
        return this
    }

    fun withVersion(sonarScannerVersion: String): SonarScannerBuilder {
        this.sonarScannerVersion = sonarScannerVersion
        return this
    }

    fun withSonarScannerPath(sonarScannerPath: String): SonarScannerBuilder {
        this.sonarScannerPath = sonarScannerPath
        return this
    }

    fun build(): SonarScanner {
        if (!this::runner.isInitialized) {
            runner = DefaultCommandRunner.getINSTANCE()
        }

        if (this::sonarScannerPath.isInitialized && sonarScannerPath.isNotEmpty()) {
            return DefaultSonarScanner(runner, sonarScannerPath)
        }

        if (!this::sonarScannerVersion.isInitialized || sonarScannerVersion.isEmpty()) {
            sonarScannerVersion =
                DEFAULT_SONAR_SCANNER_VERSION
        }

        sonarScannerPath = getOrDownloadSonarScanner(sonarScannerVersion)

        return DefaultSonarScanner(runner, sonarScannerPath)
    }

    private fun getOrDownloadSonarScanner(version: String): String {
        val url = buildDownloadUrl(version)
        val destination = File(nugetCacheDir, UrlExtension.getFileNameWithoutExtension(url))
        if (!destination.exists()) {
            val file = FileDownloader().download(url)
            ZippedFile(file).unzip(destination)
        }

        return Paths.get(destination.absolutePath)
            .resolve(SONAR_SCANNER_EXECUTABLE_NAME)
            .toAbsolutePath()
            .toString()
    }

    private fun buildDownloadUrl(version: String): URL {
        return URL("https://github.com/SonarSource/sonar-scanner-msbuild/releases/download/$version/sonar-scanner-msbuild-$version-net46.zip")
    }

    companion object {
        const val DEFAULT_SONAR_SCANNER_VERSION = "4.6.2.2108"
        const val SONAR_SCANNER_EXECUTABLE_NAME = "SonarScanner.MsBuild.exe"
    }

}