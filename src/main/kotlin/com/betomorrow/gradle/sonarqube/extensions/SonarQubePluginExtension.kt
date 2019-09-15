package com.betomorrow.gradle.sonarqube.extensions

import org.gradle.api.Project
import java.io.File

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

    var configuration: String = "Release"
    var platform: String? = null

    var nunitReport: String? = null
    var vstestReport: String? = null

    val nunitReportFile: File?
        get() {
            return nunitReport?.let { path -> project.rootDir.resolve(path) }
        }

    val vstestReportFile: File?
        get() {
            return vstestReport?.let { path -> project.rootDir.resolve(path) }
        }
}