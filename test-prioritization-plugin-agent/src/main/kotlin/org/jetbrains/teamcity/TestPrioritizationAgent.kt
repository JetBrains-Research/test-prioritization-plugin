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
        logger.message("Reordering tests...")
        val folder = feature.parameters[PrioritizationConstants.TESTS_FOLDER_KEY] ?: return
        val tests = build.checkoutDirectory.resolve(folder)
        val testsResources = tests.resolve("resources")
        val testsKotlin = tests.resolve("kotlin")
        testsKotlin.mkdirs()
        testsResources.mkdirs()

        val junitPropertiesFile = testsResources.resolve("junit-platform.properties")
        val customOrderFile = testsKotlin.resolve("CustomOrder.kt")

        val rawConfig = build.sharedConfigParameters.getValue(PrioritizationConstants.TEST_SORTED_CONFIG_KEY)
        val config = rawConfig.lineSequence().map { "\"$it\"," }.joinToString(separator = "\n" + " ".repeat(8))
        val wtf = PrioritizationConstants.WTF.replace(PrioritizationConstants.INSERT_YOUR_CONFIG_HERE, config)

        junitPropertiesFile.writeText(PrioritizationConstants.WTF2)
        customOrderFile.writeText(wtf)
    }
}
