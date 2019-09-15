package com.betomorrow.gradle.sonarqube.tools.msbuild

interface MsBuild {
    fun rebuildSolution(solutionPath: String, configuration: String, platform: String? = null): Int
}