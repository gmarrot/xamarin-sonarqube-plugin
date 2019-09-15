package com.betomorrow.gradle.sonarqube.tools.sonarscanner

import java.io.File

interface SonarScanner {
    fun setCredentials(login: String, password: String)
    fun setCredentials(authenticationToken: String)
    fun clearCredentials()

    fun begin(
        projectKey: String,
        projectName: String? = null,
        version: String? = null,
        url: String? = null,
        nunitReport: File? = null,
        vstestReport: File? = null
    ): Int

    fun end(): Int
}