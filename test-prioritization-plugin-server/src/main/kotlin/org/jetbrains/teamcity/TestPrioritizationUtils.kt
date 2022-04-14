package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.SBuild

internal object TestPrioritizationUtils {
    fun isFeatureEnabled(build: SBuild) =
        build.getBuildFeaturesOfType(PrioritizationConstants.FEATURE_TYPE).isNotEmpty()

    fun String.toRatioOrNull(): Pair<Int, Int>? {
        val splitFraction = split("/").mapNotNull { it.toIntOrNull() }
        return if (splitFraction.size == 2) {
            splitFraction[0] to splitFraction[1]
        } else null
    }
}
