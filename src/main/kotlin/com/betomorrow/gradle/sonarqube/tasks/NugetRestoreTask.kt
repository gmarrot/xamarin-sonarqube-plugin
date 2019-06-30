package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.xamarin.tools.nuget.Nuget
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

open class NugetRestoreTask : DefaultTask() {

    private val nuget = PluginContext.current.nuget

    @TaskAction
    fun restore() {
        val result = nuget.restore()
        if (result > 0) {
            throw GradleException("Can't restore packages")
        }
    }

}