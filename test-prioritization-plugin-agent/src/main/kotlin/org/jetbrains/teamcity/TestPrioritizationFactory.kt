package org.jetbrains.teamcity

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory

class TestPrioritizationFactory : CommandLineBuildServiceFactory {
    override fun createService() = TestPrioritizationService()

    override fun getBuildRunnerInfo() = object : AgentBuildRunnerInfo {
        override fun getType() = PrioritizationConstants.RUNNER_TYPE

        override fun canRun(p0: BuildAgentConfiguration) = true
    }
}
