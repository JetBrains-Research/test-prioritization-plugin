package org.jetbrains.teamcity

import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.util.EventDispatcher

private const val RESOURCES = "resources"
private const val KOTLIN = "kotlin"
private const val CUSTOM_ORDER_TEMPLATE = "/template/custom-order-template.kt"
private const val JUNIT_PROPERTIES_TEMPLATE = "/template/junit-platform-properties-template.txt"
private const val JUNIT_PROPERTIES_FILE = "junit-platform.properties"
private const val METHOD_ORDER_CONFIG = "test-prioritization-method-config.txt"
private const val CUSTOM_ORDER_FILE = "CustomOrder.kt"

class TestPrioritizationAgent(dispatcher: EventDispatcher<AgentLifeCycleListener>) : AgentLifeCycleAdapter() {
    init {
        dispatcher.addListener(this)
    }

    private fun testPrioritizationFeature(build: AgentRunningBuild) =
        build.getBuildFeaturesOfType(PrioritizationConstants.FEATURE_TYPE).firstOrNull()

    private fun getResource(name: String): String? =
        this::class.java.getResourceAsStream(name)?.bufferedReader()?.readText()

    override fun buildStarted(build: AgentRunningBuild) {
        val feature = testPrioritizationFeature(build) ?: return
        val testsFolderName = feature.parameters[PrioritizationConstants.TESTS_FOLDER_NAME_KEY] ?: return
        val methodOrderConfig = build.sharedConfigParameters[PrioritizationConstants.METHOD_ORDER_CONFIG_KEY] ?: return
        val customOrderTemplate = getResource(CUSTOM_ORDER_TEMPLATE) ?: return
        val junitPropertiesTemplate = getResource(JUNIT_PROPERTIES_TEMPLATE) ?: return

        val logger = build.buildLogger.threadLogger
        logger.message("Reordering tests...")

        val testsDir = build.checkoutDirectory.resolve(testsFolderName)
        val testsKotlin = testsDir.resolve(KOTLIN)
        val testsResources = testsDir.resolve(RESOURCES)
        testsKotlin.mkdirs()
        testsResources.mkdirs()

        testsKotlin.resolve(CUSTOM_ORDER_FILE).writeText(customOrderTemplate)
        testsResources.resolve(METHOD_ORDER_CONFIG).writeText(methodOrderConfig)
        testsResources.resolve(JUNIT_PROPERTIES_FILE).writeText(junitPropertiesTemplate)

        logger.message("Reordering tests done")
        logger.message(methodOrderConfig)
    }
}
