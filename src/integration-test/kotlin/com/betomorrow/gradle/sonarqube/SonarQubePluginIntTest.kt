package com.betomorrow.gradle.sonarqube

import org.assertj.core.api.Assertions.assertThat
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class SonarQubePluginIntTest {

    val testProjectDir = TemporaryFolder()

    lateinit var buildFile: File

    @BeforeEach
    fun setUp() {
        testProjectDir.create()
        testProjectDir.newFile("sample.sln")
        buildFile = testProjectDir.newFile("build.gradle")
        buildFile.appendText(
            """
            plugins {
                id 'com.betomorrow.dotnet.sonarqube'
            }

            """.trimIndent()
        )
    }

    @Test
    fun `test plugin should be applied successfully and create nugetRestore and sonarScan tasks`() {
        // When
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks", "--stacktrace", "--all")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        assertThat(result.task(":tasks")!!.outcome).isEqualTo(TaskOutcome.SUCCESS)

        assertThat(result.output).contains("nugetRestore -")
        assertThat(result.output).contains("sonarScan -")
    }

    @Test
    fun `test plugin should be applied successfully with Gradle 4_9`() {
        // When
        val result = GradleRunner.create()
            .withGradleVersion("4.9")
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks", "--stacktrace", "--all")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        assertThat(result.task(":tasks")!!.outcome).isEqualTo(TaskOutcome.SUCCESS)

        assertThat(result.output).contains("nugetRestore -")
        assertThat(result.output).contains("sonarScan -")
    }

    @Test
    fun `test plugin should register that sonarScan task depends on nugetRestore`() {
        // When
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("sonarScan", "-PdryRun=true")
            .withPluginClasspath()
            .withDebug(true)
            .build()

        // Then
        println(result.tasks)
        assertThat(result.tasks.map { it.path }).containsExactly(":nugetRestore", ":sonarScan")
    }

}