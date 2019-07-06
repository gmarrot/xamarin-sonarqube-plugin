package com.betomorrow.gradle.sonarqube.tools.sonarscanner

interface SonarScanner {

    fun setCredentials(login: String, password: String)
    fun setCredentials(authenticationToken: String)
    fun clearCredentials()

    fun begin(projectKey: String, projectName: String? = null, version: String? = null, url: String? = null): Int

    fun end(): Int

}