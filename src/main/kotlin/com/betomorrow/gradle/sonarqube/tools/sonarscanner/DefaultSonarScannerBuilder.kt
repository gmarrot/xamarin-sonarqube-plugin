package com.betomorrow.gradle.sonarqube.tools.sonarscanner

import com.betomorrow.groovy.extensions.UrlExtension
import com.betomorrow.xamarin.commands.CommandRunner
import com.betomorrow.xamarin.commands.DefaultCommandRunner
import com.betomorrow.xamarin.files.FileDownloader
import com.betomorrow.xamarin.files.ZippedFile
import java.io.File
import java.net.URL
import java.nio.file.Paths

class DefaultSonarScannerBuilder : SonarScannerBuilder {

    private lateinit var runner: CommandRunner

    private var scannerVersion: String? = null
    private var scannerPath: String? = null

    private val sonarqubeCacheDir: String
        get() {
            return Paths.get(System.getProperty("user.home"))
                .resolve(".sonarqube")
                .resolve("caches")
                .toAbsolutePath()
                .toString()
        }

    override fun withCommandBuilder(runner: CommandRunner): SonarScannerBuilder {
        this.runner = runner
        return this
    }

    override fun withVersion(version: String): SonarScannerBuilder {
        scannerVersion = version
        return this
    }

    override fun withSonarScannerPath(path: String): SonarScannerBuilder {
        scannerPath = path
        return this
    }

    override fun build(): SonarScanner {
        if (!this::runner.isInitialized) {
            runner = DefaultCommandRunner.getINSTANCE()
        }

        scannerPath?.let { path ->
            if (path.isNotEmpty()) {
                return DefaultSonarScanner(runner, path)
            }
        }

        val version = scannerVersion ?: DEFAULT_SONAR_SCANNER_VERSION
        val path = getOrDownloadSonarScanner(version)

        return DefaultSonarScanner(runner, path)
    }

    private fun getOrDownloadSonarScanner(version: String): String {
        val url = buildDownloadUrl(version)
        val destination = File(sonarqubeCacheDir, UrlExtension.getFileNameWithoutExtension(url))
        if (!destination.exists()) {
            val file = FileDownloader().download(url)
            ZippedFile(file).unzip(destination)
            // Sonar Scanner for MSBuild releases has the sonar-scanner binary not executable on Unix file system
            fixSonarScannerBinaryRights(destination)
        }

        return Paths.get(destination.absolutePath)
            .resolve(SONAR_SCANNER_EXECUTABLE_NAME)
            .toAbsolutePath()
            .toString()
    }

    private fun fixSonarScannerBinaryRights(scannerDirectory: File) {
        val sonarScannerBinary = scannerDirectory
            .walkTopDown()
            .find { file -> file.isFile && file.name == "sonar-scanner" }

        sonarScannerBinary?.let { binary ->
            if (!binary.canExecute()) {
                binary.setExecutable(true, true)
            }
        }
    }

    private fun buildDownloadUrl(version: String): URL {
        return URL("https://github.com/SonarSource/sonar-scanner-msbuild/releases/download/$version/sonar-scanner-msbuild-$version-net46.zip")
    }

    companion object {
        const val DEFAULT_SONAR_SCANNER_VERSION = "4.6.2.2108"
        const val SONAR_SCANNER_EXECUTABLE_NAME = "SonarScanner.MsBuild.exe"
    }

}