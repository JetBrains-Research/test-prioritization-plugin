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
        build.fullStatistics.allTests.forEach { test ->
            val name = test.test.name.asString
            val isFailed = test.status.isFailed
            val was = storage.getValue(name) ?: "0/0"
            var (failed, all) = was.split("/").map { it.toInt() }
            if (isFailed) failed += 1
            all += 1
            storage.putValue(name, "$failed/$all")
        }
        storage.flush()
        Loggers.SERVER.warn(storage.values?.entries?.joinToString(separator = "\n") { "${it.key}: ${it.value}" })
    }
}
