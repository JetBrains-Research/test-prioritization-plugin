package org.jetbrains.teamcity

import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine
import jetbrains.buildServer.serverSide.TeamCityProperties

class EchoService : BuildServiceAdapter() {
    override fun makeProgramCommandLine(): ProgramCommandLine {
        val message = TeamCityProperties.getPropertyOrNull("teamcity.tests.runRiskGroupTestsFirst") ?: "nevazhno"
        return SimpleProgramCommandLine(runnerContext, "/bin/echo", listOf(message))
    }
}
