package org.jetbrains.teamcity

import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.serverSide.BuildServerAdapter
import jetbrains.buildServer.serverSide.BuildServerListener
import jetbrains.buildServer.serverSide.SRunningBuild
import jetbrains.buildServer.util.EventDispatcher
import org.jetbrains.teamcity.TestPrioritizationUtils.isTestPrioritizationEnabled
import org.jetbrains.teamcity.TestPrioritizationUtils.toRatioOrNull

class TestPrioritizationBuildServer(dispatcher: EventDispatcher<BuildServerListener>) : BuildServerAdapter() {
    init {
        dispatcher.addListener(this)
    }

    override fun buildFinished(build: SRunningBuild) {
        if (!build.isTestPrioritizationEnabled()) return
        val storage = build.buildType?.getCustomDataStorage(PrioritizationConstants.FEATURE_NAME) ?: return

        val testsByName = build.fullStatistics.allTests.groupingBy { it.test.name.nameWithoutParameters }
        testsByName.fold(true) { success, p ->
            success && p.status.isSuccessful
        }.forEach { (name, isSuccessful) ->
            val fraction = storage.getValue(name) ?: "0/0"
            var (successful, all) = fraction.toRatioOrNull() ?: return@forEach
            if (isSuccessful) successful += 1
            all += 1
            storage.putValue(name, "$successful/$all")
        }
        storage.flush()
        Loggers.SERVER.info(storage.values?.entries?.joinToString(separator = "\n") { "${it.key}: ${it.value}" })
    }
}
