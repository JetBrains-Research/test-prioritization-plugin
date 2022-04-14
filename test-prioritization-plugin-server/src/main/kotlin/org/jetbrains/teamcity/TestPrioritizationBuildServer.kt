package org.jetbrains.teamcity

import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.serverSide.BuildServerAdapter
import jetbrains.buildServer.serverSide.BuildServerListener
import jetbrains.buildServer.serverSide.SRunningBuild
import jetbrains.buildServer.util.EventDispatcher

class TestPrioritizationBuildServer(dispatcher: EventDispatcher<BuildServerListener>) : BuildServerAdapter() {
    init {
        dispatcher.addListener(this)
    }

    override fun buildFinished(build: SRunningBuild) {
        val features = build.getBuildFeaturesOfType(PrioritizationConstants.FEATURE_TYPE)
        if (features.isEmpty()) return
        val storage = build.buildType?.getCustomDataStorage(PrioritizationConstants.FEATURE_NAME) ?: return
        build.fullStatistics.allTests.groupingBy { it.test.name.nameWithoutParameters }.fold(true) { success, p ->
            success && p.status.isSuccessful
        }.forEach { (name, isSuccessful) ->
            val was = storage.getValue(name) ?: "0/0"
            var (successful, all) = was.split("/").map { it.toInt() }
            if (isSuccessful) successful += 1
            all += 1
            storage.putValue(name, "$successful/$all")
        }
        storage.flush()
        Loggers.SERVER.info(storage.values?.entries?.joinToString(separator = "\n") { "${it.key}: ${it.value}" })
    }
}
