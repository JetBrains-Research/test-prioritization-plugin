package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.BuildStartContext
import jetbrains.buildServer.serverSide.BuildStartContextProcessor
import org.jetbrains.teamcity.TestPrioritizationUtils.isFeatureEnabled
import org.jetbrains.teamcity.TestPrioritizationUtils.toRatioOrNull

class TestPrioritizationContextProcessor : BuildStartContextProcessor {
    override fun updateParameters(context: BuildStartContext) {
        val build = context.build
        if (!isFeatureEnabled(build)) return

        val storage = build.buildType?.getCustomDataStorage(PrioritizationConstants.FEATURE_NAME)?.values ?: return
        val config = storage.entries.mapNotNull { (name, fraction) ->
            val (successful, all) = fraction.toRatioOrNull() ?: return@mapNotNull null
            name to successful.toDouble() / all
        }.sortedBy { it.second }.joinToString(separator = "\n") { it.first }

        context.addSharedParameter(PrioritizationConstants.METHOD_ORDER_CONFIG_KEY, config)
    }
}
