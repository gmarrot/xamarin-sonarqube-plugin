package com.betomorrow.gradle.sonarqube.tasks

import com.betomorrow.gradle.sonarqube.context.PluginContext
import com.betomorrow.gradle.sonarqube.tools.msbuild.MsBuild
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScanner
import com.betomorrow.gradle.sonarqube.tools.sonarscanner.SonarScannerBuilder
import com.betomorrow.xamarin.tools.nuget.Nuget
import com.nhaarman.mockitokotlin2.anyOrNull
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.GradleException
import org.gradle.internal.impldep.org.junit.Rule
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SonarScanTaskTest {

    @Rule
    val tempProjectDir = TemporaryFolder()

    private lateinit var sonarScanTask: SonarScanTask

    @Mock
    private lateinit var nuget: Nuget

    @Mock
    private lateinit var sonarScannerBuilder: SonarScannerBuilder

    @Mock
    private lateinit var sonarScanner: SonarScanner

    @Mock
    private lateinit var msBuild: MsBuild

    @InjectMocks
    private lateinit var fakePluginContext: PluginContext

    @BeforeEach
    fun setUp() {
        PluginContext.current = fakePluginContext

        val project = ProjectBuilder.builder().build()
        sonarScanTask = project.task(mapOf("type" to SonarScanTask::class.java), "sonarScan") as SonarScanTask
        sonarScanTask.projectKey = PROJECT_KEY

        tempProjectDir.create()
        val solutionFile = tempProjectDir.newFile("project.sln")
        sonarScanTask.solutionFile = solutionFile

        given(sonarScannerBuilder.build()).willReturn(sonarScanner)
    }

    @Test
    fun `test scan should initialize scanner with version when defined`() {
        // Given
        sonarScanTask.sonarScannerVersion = "1.0.1"

        // When
        sonarScanTask.scan()

        // Then
        then(sonarScannerBuilder).should(times(1)).withVersion("1.0.1")
    }

    @Test
    fun `test scan should initialize scanner with path when defined`() {
        // Given
        sonarScanTask.sonarScannerPath = "/tmp/sonar-scanner"

        // When
        sonarScanTask.scan()

        // Then
        then(sonarScannerBuilder).should(times(1)).withSonarScannerPath("/tmp/sonar-scanner")
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
        then(sonarScanner).should(times(1)).setCredentials("AuthenticationTok3n")
        then(sonarScanner).should(never()).setCredentials(anyString(), anyString())
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
        given(sonarScanner.begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())).willReturn(1)

        // When
        val thrown = Assertions.catchThrowable(sonarScanTask::scan)

        // Then
        assertThat(thrown).isInstanceOf(GradleException::class.java)

        then(sonarScanner).should(times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        then(msBuild).should(never()).rebuildSolution(anyString(), anyString(), anyOrNull())
        then(sonarScanner).should(never()).end()
    }

    @Test
    fun `test scan should call exit with exception when solution build failed`() {
        // Given
        given(msBuild.rebuildSolution(anyString(), anyString(), anyOrNull())).willReturn(1)

        // When
        val thrown = Assertions.catchThrowable(sonarScanTask::scan)

        // Then
        assertThat(thrown).isInstanceOf(GradleException::class.java)

        then(sonarScanner).should(times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        then(msBuild).should(times(1)).rebuildSolution(anyString(), anyString(), anyOrNull())
        then(sonarScanner).should(never()).end()
    }

    @Test
    fun `test scan should call exit with exception when scanner end failed`() {
        // Given
        given(sonarScanner.end()).willReturn(1)

        // When
        val thrown = Assertions.catchThrowable(sonarScanTask::scan)

        // Then
        assertThat(thrown).isInstanceOf(GradleException::class.java)

        then(sonarScanner).should(times(1)).begin(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        then(msBuild).should(times(1)).rebuildSolution(anyString(), anyString(), anyOrNull())
        then(sonarScanner).should(times(1)).end()
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
        then(sonarScanner).should(times(1))
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
        then(msBuild).should(times(1))
            .rebuildSolution(sonarScanTask.solutionFile.absolutePath, "CustomConfiguration", "CustomPlatform")
    }

    companion object {
        const val PROJECT_KEY = "project-key"
    }

}