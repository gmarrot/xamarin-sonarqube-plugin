package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.gradle.sonarqube.tools.msbuild.MsBuild
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScanner
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScannerBuilder
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.GradleException
import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString

class SonarScanTaskTest {
    @Rule
    val tempProjectDir = TemporaryFolder()

    private lateinit var sonarScanTask: SonarScanTask

    private val sonarScanner = mock<SonarScanner> { }
    private val sonarScannerBuilder = mock<SonarScannerBuilder> {
        on { build() } doReturn sonarScanner
    }
    private val msBuild = mock<MsBuild> { }

    private val fakePluginContext = mock<PluginContext> {
        on { sonarScannerBuilder } doReturn sonarScannerBuilder
        on { msBuild } doReturn msBuild
    }

    @BeforeEach
    fun setUp() {
        PluginContext.current = fakePluginContext

        val project = ProjectBuilder.builder().build()
        sonarScanTask = project.task(mapOf("type" to SonarScanTask::class.java), "sonarScan") as SonarScanTask
        sonarScanTask.projectKey = PROJECT_KEY

        tempProjectDir.create()
        val solutionFile = tempProjectDir.newFile("project.sln")
        sonarScanTask.solutionFile = solutionFile
    }

    @Test
    fun `test scan should initialize scanner with version when defined`() {
        // Given
        sonarScanTask.sonarScannerVersion = "1.0.1"

        // When
        sonarScanTask.scan()

        // Then
        verify(sonarScannerBuilder, times(1)).withVersion("1.0.1")
    }

    @Test
    fun `test scan should initialize scanner with path when defined`() {
        // Given
        sonarScanTask.sonarScannerPath = "/tmp/sonar-scanner"

        // When
        sonarScanTask.scan()

        // Then
        verify(sonarScannerBuilder, times(1)).withSonarScannerPath("/tmp/sonar-scanner")
    }

    @Test
    fun `test scan should set login and password to scanner before begin scan when defined`() {
        // Given
        sonarScanTask.serverLogin = "Username"
        sonarScanTask.serverPassword = "Secr3tPassword"

        // When
        sonarScanTask.scan()

        // Then
        val inOrder = inOrder(sonarScanner)
        inOrder.verify(sonarScanner, times(1)).setCredentials("Username", "Secr3tPassword")
        inOrder.verify(sonarScanner, times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun `test scan should set authentication token to scanner before begin scan when defined`() {
        // Given
        sonarScanTask.serverAuthenticationToken = "AuthenticationTok3n"

        // When
        sonarScanTask.scan()

        // Then
        val inOrder = inOrder(sonarScanner)
        inOrder.verify(sonarScanner, times(1)).setCredentials("AuthenticationTok3n")
        inOrder.verify(sonarScanner, times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }

    @Test
    fun `test scan should set authentication token instead of login and password to scanner when all properties are defined`() {
        // Given
        sonarScanTask.serverLogin = "Username"
        sonarScanTask.serverPassword = "Secr3tPassword"
        sonarScanTask.serverAuthenticationToken = "AuthenticationTok3n"

        // When
        sonarScanTask.scan()

        // Then
        verify(sonarScanner, times(1)).setCredentials("AuthenticationTok3n")
        verify(sonarScanner, never()).setCredentials(anyString(), anyString())
    }

    @Test
    fun `test scan should call scanner begin then solution build then scan end`() {
        // When
        sonarScanTask.scan()

        // Then
        val inOrder = inOrder(sonarScanner, msBuild)
        inOrder.verify(sonarScanner, times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        inOrder.verify(msBuild, times(1)).rebuildSolution(anyString(), anyString(), anyOrNull())
        inOrder.verify(sonarScanner, times(1)).end()
    }

    @Test
    fun `test scan should call exit with exception when scanner begin failed`() {
        // Given
        whenever(sonarScanner.begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(1)

        // When
        val thrown = Assertions.catchThrowable(sonarScanTask::scan)

        // Then
        assertThat(thrown).isInstanceOf(GradleException::class.java)

        verify(sonarScanner, times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        verify(msBuild, never()).rebuildSolution(anyString(), anyString(), anyOrNull())
        verify(sonarScanner, never()).end()
    }

    @Test
    fun `test scan should call exit with exception when solution build failed`() {
        // Given
        whenever(msBuild.rebuildSolution(anyString(), anyString(), anyOrNull())).thenReturn(1)

        // When
        val thrown = Assertions.catchThrowable(sonarScanTask::scan)

        // Then
        assertThat(thrown).isInstanceOf(GradleException::class.java)

        verify(sonarScanner, times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        verify(msBuild, times(1)).rebuildSolution(anyString(), anyString(), anyOrNull())
        verify(sonarScanner, never()).end()
    }

    @Test
    fun `test scan should call exit with exception when scanner end failed`() {
        // Given
        whenever(sonarScanner.end()).thenReturn(1)

        // When
        val thrown = Assertions.catchThrowable(sonarScanTask::scan)

        // Then
        assertThat(thrown).isInstanceOf(GradleException::class.java)

        verify(sonarScanner, times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        verify(msBuild, times(1)).rebuildSolution(anyString(), anyString(), anyOrNull())
        verify(sonarScanner, times(1)).end()
    }

    @Test
    fun `test scan should call scanner begin with task properties`() {
        // Given
        sonarScanTask.projectName = "Project Name"
        sonarScanTask.projectVersion = "1.0.1"
        sonarScanTask.serverUrl = "http://sonarqube-server.url"

        val nunitReport = tempProjectDir.newFile("nunit-report.xml")
        sonarScanTask.nunitReport = nunitReport

        // When
        sonarScanTask.scan()

        // Then
        verify(sonarScanner, times(1))
            .begin(PROJECT_KEY, "Project Name", "1.0.1", "http://sonarqube-server.url", nunitReport)
    }

    @Test
    fun `test scan should call solution build with task properties`() {
        // Given
        sonarScanTask.configuration = "CustomConfiguration"
        sonarScanTask.platform = "CustomPlatform"

        // When
        sonarScanTask.scan()

        // Then
        verify(msBuild, times(1))
            .rebuildSolution(sonarScanTask.solutionFile.absolutePath, "CustomConfiguration", "CustomPlatform")
    }

    companion object {
        const val PROJECT_KEY = "project-key"
    }
}