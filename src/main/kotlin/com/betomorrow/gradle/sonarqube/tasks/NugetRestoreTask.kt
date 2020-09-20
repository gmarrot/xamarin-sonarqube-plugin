package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

open class NugetRestoreTask : DefaultTask() {
    @TaskAction
    fun restore() {
        val nuget = PluginContext.current.nuget

        val result = nuget.restore()
        if (result > 0) {
            throw GradleException("Can't restore packages")
        }
    }
}
