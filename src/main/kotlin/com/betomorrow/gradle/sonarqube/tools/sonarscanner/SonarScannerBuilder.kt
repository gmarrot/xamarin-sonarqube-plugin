package com.betomorrow.gradle.sonarqube.tools.sonarscanner

import com.betomorrow.xamarin.commands.CommandRunner

interface SonarScannerBuilder {

    fun withCommandBuilder(runner: CommandRunner): SonarScannerBuilder
    fun withVersion(version: String): SonarScannerBuilder
    fun withSonarScannerPath(path: String): SonarScannerBuilder

    fun build(): SonarScanner

}