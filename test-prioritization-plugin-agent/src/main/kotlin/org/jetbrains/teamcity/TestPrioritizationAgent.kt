package org.jetbrains.teamcity

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpGet
import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.AgentRuntimeProperties.TEAMCITY_SERVER_URL
import jetbrains.buildServer.agent.ServerProvidedProperties.TEAMCITY_AUTH_PASSWORD_PROP
import jetbrains.buildServer.agent.ServerProvidedProperties.TEAMCITY_AUTH_USER_ID_PROP
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher
import jetbrains.buildServer.util.EventDispatcher
import org.jetbrains.teamcity.PrioritizationConstants.ARTIFACT_CONFIG_NAME
import org.jetbrains.teamcity.PrioritizationConstants.ARTIFACT_CONFIG_PATH
import org.jetbrains.teamcity.PrioritizationConstants.ARTIFACT_FOLDER_PATH

private const val RESOURCES = "resources"
private const val JAVA = "java"
private const val CUSTOM_ORDER_FILE = "CustomOrder.java"
private const val JUNIT_PROPERTIES_FILE = "junit-platform.properties"
private const val CUSTOM_ORDER_TEMPLATE = "/template/$CUSTOM_ORDER_FILE"
private const val JUNIT_PROPERTIES_TEMPLATE = "/template/$JUNIT_PROPERTIES_FILE"
private const val TEST_PRIORITIZATION_CONFIG = "test-prioritization-config.txt"

class TestPrioritizationAgent(
    private val artifactsWatcher: ArtifactsWatcher,
    dispatcher: EventDispatcher<AgentLifeCycleListener>
) : AgentLifeCycleAdapter() {
    init {
        dispatcher.addListener(this)
    }

    private fun testPrioritizationFeature(build: AgentRunningBuild) =
        build.getBuildFeaturesOfType(PrioritizationConstants.FEATURE_TYPE).firstOrNull()

    private fun getResource(name: String): String? =
        this::class.java.getResourceAsStream(name)?.bufferedReader()?.readText()

    private fun getPreviousConfig(build: AgentRunningBuild): String {
        val previousBuildId = build.sharedConfigParameters[PrioritizationConstants.PREVIOUS_BUILD_KEY] ?: return ""
        val serverURL = build.sharedConfigParameters[TEAMCITY_SERVER_URL] ?: return ""
        val username = build.sharedBuildParameters.allParameters[TEAMCITY_AUTH_USER_ID_PROP] ?: return ""
        val password = build.sharedBuildParameters.allParameters[TEAMCITY_AUTH_PASSWORD_PROP] ?: return ""

        build.buildLogger.threadLogger.message("Previous build found: $previousBuildId")

        val url = "$serverURL/app/rest/builds/$previousBuildId/artifacts/content/$ARTIFACT_CONFIG_PATH"
        val (_, response, result) = url.httpGet()
            .authentication()
            .basic(username, password)
            .responseString()

        return if (response.isSuccessful) result.get() else ""
    }

    override fun preparationFinished(build: AgentRunningBuild) {
        val feature = testPrioritizationFeature(build) ?: return
        val customOrderTemplate = getResource(CUSTOM_ORDER_TEMPLATE) ?: return
        val junitPropertiesTemplate = getResource(JUNIT_PROPERTIES_TEMPLATE) ?: return
        val testsRootsPaths = feature.parameters[PrioritizationConstants.TESTS_FOLDER_ROOT_PATHS_KEY] ?: return
        if (testsRootsPaths.isEmpty()) return

        val logger = build.buildLogger.threadLogger
        logger.message("Reordering tests...")

        val config = getPreviousConfig(build)
        build.checkoutDirectory.resolve(ARTIFACT_CONFIG_NAME).writeText(config)
        artifactsWatcher.addNewArtifactsPath("$ARTIFACT_CONFIG_NAME => $ARTIFACT_FOLDER_PATH")

        testsRootsPaths.lineSequence().forEach { testsRootPath ->
            val testsDir = build.checkoutDirectory.resolve(testsRootPath)
            val testsJava = testsDir.resolve(JAVA)
            val testsResources = testsDir.resolve(RESOURCES)
            testsJava.mkdirs()
            testsResources.mkdirs()

            testsResources.resolve(TEST_PRIORITIZATION_CONFIG).writeText(config)
            testsJava.resolve(CUSTOM_ORDER_FILE).writeText(customOrderTemplate)
            testsResources.resolve(JUNIT_PROPERTIES_FILE).writeText(junitPropertiesTemplate)
        }

        logger.message("Reordering tests done")
    }
}
