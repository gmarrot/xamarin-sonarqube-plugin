package com.betomorrow.gradle.sonarqube.extensions

import org.gradle.api.Project

const val SONARQUBE_EXTENSION_NAME = "sonarqube"

open class SonarQubePluginExtension(private val project: Project) {

    // Server configuration

    var url: String? = null
    var login: String? = null
    var password: String? = null
    var authenticationToken: String? = null

    // Project configuration

    lateinit var projectKey: String
    var projectName: String? = null

}