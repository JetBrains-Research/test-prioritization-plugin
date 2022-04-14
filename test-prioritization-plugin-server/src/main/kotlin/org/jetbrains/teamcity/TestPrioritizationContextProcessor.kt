package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.BuildStartContext
import jetbrains.buildServer.serverSide.BuildStartContextProcessor

class TestPrioritizationContextProcessor : BuildStartContextProcessor {
    override fun updateParameters(context: BuildStartContext) {
        val build = context.build
        val storage = build.buildType?.getCustomDataStorage(PrioritizationConstants.FEATURE_NAME)?.values ?: return
        val config = storage.entries.sortedBy { (_, freq) ->
            val (successful, all) = freq.split("/").map { it.toInt() }
            successful.toDouble() / all
        }.joinToString(separator = "\n") {
            it.key
        }
        context.addSharedParameter(PrioritizationConstants.TEST_SORTED_CONFIG_KEY, config)
    }
}
