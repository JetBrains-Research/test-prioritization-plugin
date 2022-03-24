package org.jetbrains.teamcity

import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory

class EchoFactory : CommandLineBuildServiceFactory {
    override fun createService() = EchoService()

    override fun getBuildRunnerInfo() = object : AgentBuildRunnerInfo {
        override fun getType() = Constants.RUNNER_TYPE

        override fun canRun(p0: BuildAgentConfiguration) = true
    }
}
