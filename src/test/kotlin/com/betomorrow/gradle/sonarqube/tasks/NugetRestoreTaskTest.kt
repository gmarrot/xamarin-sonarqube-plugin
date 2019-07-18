package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.gradle.sonarqube.tools.msbuild.MsBuild
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScannerBuilder
import com.betomorrow.xamarin.tools.nuget.Nuget
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class NugetRestoreTaskTest {

    private lateinit var nugetRestoreTask: NugetRestoreTask

    @Mock
    private lateinit var nuget: Nuget

    @Mock
    private lateinit var sonarScannerBuilder: SonarScannerBuilder

    @Mock
    private lateinit var msBuild: MsBuild

    @InjectMocks
    private lateinit var fakePluginContext: PluginContext

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
        given(nuget.restore()).willReturn(0)

        // When
        nugetRestoreTask.restore()

        // Then
        then(nuget).should(times(1)).restore()
    }

    @Test
    fun `test restore should fail when NuGet restore return positive value`() {
        // Given
        given(nuget.restore()).willReturn(1)

        // When
        val thrown = catchThrowable(nugetRestoreTask::restore)

        // Then
        then(nuget).should(times(1)).restore()
        assertThat(thrown).isInstanceOf(GradleException::class.java)
    }

}