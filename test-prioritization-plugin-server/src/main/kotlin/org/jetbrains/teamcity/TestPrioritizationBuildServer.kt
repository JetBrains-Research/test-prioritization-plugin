package org.jetbrains.teamcity

import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.serverSide.BuildServerAdapter
import jetbrains.buildServer.serverSide.BuildServerListener
import jetbrains.buildServer.serverSide.SBuild
import jetbrains.buildServer.serverSide.SRunningBuild
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode
import jetbrains.buildServer.util.EventDispatcher
import org.jetbrains.teamcity.PrioritizationConstants.ARTIFACT_CONFIG_PATH
import org.jetbrains.teamcity.TestPrioritizationUtils.isTestPrioritizationEnabled
import org.jetbrains.teamcity.TestPrioritizationUtils.toRatioOrNull

class TestPrioritizationBuildServer(dispatcher: EventDispatcher<BuildServerListener>) : BuildServerAdapter() {
    init {
        dispatcher.addListener(this)
    }

    private fun getConfigFromArtifact(build: SBuild): MutableMap<String, Pair<Int, Int>?> {
        val artifact = build.getArtifacts(BuildArtifactsViewMode.VIEW_HIDDEN_ONLY).getArtifact(ARTIFACT_CONFIG_PATH)
        return artifact?.inputStream?.bufferedReader()?.readText()?.lines()?.associate {
            it.substringBeforeLast(":") to it.substringAfterLast(":").toRatioOrNull()
        }?.toMutableMap() ?: mutableMapOf()
    }

    override fun buildFinished(build: SRunningBuild) {
        if (!build.isTestPrioritizationEnabled()) return

        val config = getConfigFromArtifact(build)

        val testsByName = build.fullStatistics.allTests.groupingBy {
            it.test.name.nameWithoutParameters
        }.fold(true) { success, p ->
            success && p.status.isSuccessful
        }

        testsByName.forEach { (name, isSuccessful) ->
            var (successful, all) = config[name] ?: (0 to 0)
            if (isSuccessful) successful += 1
            all += 1
            config[name] = successful to all
        }

        val newConfigString = config.mapNotNull { (name, ratio) ->
            ratio?.let { (successful, all) ->
                "$name:$successful/$all"
            }
        }.joinToString(separator = "\n")

        val configFile = build.artifactsDirectory.resolve(ARTIFACT_CONFIG_PATH)
        configFile.writeText(newConfigString)
        Loggers.SERVER.info(newConfigString)
    }
}
