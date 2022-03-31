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
        val logger = build.buildLogger.threadLogger
        logger.message("Reordering tests...")
        val folder = features.first().parameters[PrioritizationConstants.TESTS_FOLDER_KEY]!!
        val tests = build.checkoutDirectory.resolve(folder)
        logger.message(tests.path)
    }
}
