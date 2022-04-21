package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.BuildStartContext
import jetbrains.buildServer.serverSide.BuildStartContextProcessor
import org.jetbrains.teamcity.TestPrioritizationUtils.isTestPrioritizationEnabled
import org.jetbrains.teamcity.TestPrioritizationUtils.toRatioOrNull

class TestPrioritizationContextProcessor : BuildStartContextProcessor {
    override fun updateParameters(context: BuildStartContext) {
        val build = context.build
        if (!build.isTestPrioritizationEnabled()) return

        val storage = build.buildType?.getCustomDataStorage(PrioritizationConstants.FEATURE_NAME)?.values ?: return
        val config = storage.entries.mapNotNull { (name, fraction) ->
            fraction.toRatioOrNull()?.let { (successful, all) ->
                "$name:${successful.toDouble() / all}"
            }
        }.joinToString(separator = "\n")

        context.addSharedParameter(PrioritizationConstants.CONFIG_KEY, config)
    }
}
