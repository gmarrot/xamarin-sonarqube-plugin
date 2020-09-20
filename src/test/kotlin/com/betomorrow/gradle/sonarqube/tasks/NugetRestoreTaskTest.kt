package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.xamarin.tools.nuget.Nuget
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times

class NugetRestoreTaskTest {
    private lateinit var nugetRestoreTask: NugetRestoreTask

    private val nuget = mock<Nuget> { }

    private val fakePluginContext = mock<PluginContext> {
        on { nuget } doReturn nuget
    }

    @BeforeEach
    fun setUp() {
        PluginContext.current = fakePluginContext

        val project = ProjectBuilder.builder().build()
        nugetRestoreTask = project.task(
            mapOf("type" to NugetRestoreTask::class.java),
            "nugetRestore"
        ) as NugetRestoreTask
    }

    @Test
    fun `test restore should succeed when NuGet restore return 0`() {
        // Given
        whenever(nuget.restore()).thenReturn(0)

        // When
        nugetRestoreTask.restore()

        // Then
        verify(nuget, times(1)).restore()
    }

    @Test
    fun `test restore should fail when NuGet restore return positive value`() {
        // Given
        whenever(nuget.restore()).thenReturn(1)

        // When
        val thrown = catchThrowable(nugetRestoreTask::restore)

        // Then
        verify(nuget, times(1)).restore()
        assertThat(thrown).isInstanceOf(GradleException::class.java)
    }
}
