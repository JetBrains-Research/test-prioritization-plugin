package org.jetbrains.teamcity

import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.util.EventDispatcher

class TestPrioritizationAgent(dispatcher: EventDispatcher<AgentLifeCycleListener>) : AgentLifeCycleAdapter() {
    init {
        dispatcher.addListener(this)
    }

    override fun buildStarted(build: AgentRunningBuild) {
        val features = build.getBuildFeaturesOfType(PrioritizationConstants.FEATURE_TYPE)
        if (features.isEmpty()) return
        val feature = features.first()
        val logger = build.buildLogger.threadLogger
        val folder = feature.parameters[PrioritizationConstants.TESTS_FOLDER_KEY] ?: return
        val tests = build.checkoutDirectory.resolve(folder)
        val testsResources = tests.resolve("resources")
        val testsKotlin = tests.resolve("kotlin")
        testsKotlin.mkdirs()
        testsResources.mkdirs()

        val WTF = this::class.java.getResourceAsStream("/template/custom-order-template.kt")?.bufferedReader()?.readText() ?: return
        val WTF2 = this::class.java.getResourceAsStream("/template/junit-platform-properties-template.txt")?.bufferedReader()?.readText() ?: return

        logger.message("Reordering tests...")

        val junitPropertiesFile = testsResources.resolve("junit-platform.properties")
        val methodSortingConfigFile = testsResources.resolve("test-prioritization-method-config.txt")
        val customOrderFile = testsKotlin.resolve("CustomOrder.kt")

        val config = build.sharedConfigParameters.getValue(PrioritizationConstants.TEST_SORTED_CONFIG_KEY)

        methodSortingConfigFile.writeText(config)
        customOrderFile.writeText(WTF)
        junitPropertiesFile.writeText(WTF2)

        logger.message("DONE")
        logger.message(methodSortingConfigFile.readText())
    }
}
