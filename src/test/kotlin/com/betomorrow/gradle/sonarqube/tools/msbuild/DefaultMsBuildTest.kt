package com.betomorrow.gradle.sonarqube.tools.msbuild

import com.betomorrow.xamarin.commands.CommandRunner
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DefaultMsBuildTest {
    private lateinit var msBuild: MsBuild

    private lateinit var commandRunner: CommandRunner
    private var executedCommand: CommandRunner.Cmd? = null

    @BeforeEach
    fun setUp() {
        commandRunner = object : CommandRunner {
            override fun setVerbose(verbose: Boolean) {
                // Nothing to do
            }

            override fun run(cmd: CommandRunner.Cmd?): Int {
                executedCommand = cmd
                return 0
            }
        }

        msBuild = DefaultMsBuild(commandRunner)
    }

    @Test
    fun `test rebuildSolution should run solution rebuild with given solution file and configuration`() {
        // When
        msBuild.rebuildSolution("sample.sln", "Release")

        // Then
        Assertions.assertThat(executedCommand).isNotNull
        Assertions.assertThat(executedCommand!!.build()).containsExactly(
            "/Library/Frameworks/Mono.framework/Commands/msbuild",
            "/t:Rebuild",
            "/p:Configuration=Release",
            "sample.sln"
        )
    }

    @Test
    fun `test rebuildSolution should run solution rebuild with given solution file and configuration and platform`() {
        // When
        msBuild.rebuildSolution("sample.sln", "Release", "Any CPU")

        // Then
        Assertions.assertThat(executedCommand).isNotNull
        Assertions.assertThat(executedCommand!!.build()).containsExactly(
            "/Library/Frameworks/Mono.framework/Commands/msbuild",
            "/t:Rebuild",
            "/p:Platform=Any CPU",
            "/p:Configuration=Release",
            "sample.sln"
        )
    }

    @Test
    fun `test rebuildSolution should run solution rebuild with given MSBuild path`() {
        // Given
        val msBuild = DefaultMsBuild(commandRunner, "/test/msbuild")

        // When
        msBuild.rebuildSolution("sample.sln", "Release")

        // Then
        Assertions.assertThat(executedCommand).isNotNull
        Assertions.assertThat(executedCommand!!.build()).containsExactly(
            "/test/msbuild",
            "/t:Rebuild",
            "/p:Configuration=Release",
            "sample.sln"
        )
    }
}
