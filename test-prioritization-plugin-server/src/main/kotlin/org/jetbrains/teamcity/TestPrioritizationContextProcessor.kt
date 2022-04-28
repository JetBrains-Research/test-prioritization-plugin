package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.BuildStartContext
import jetbrains.buildServer.serverSide.BuildStartContextProcessor
import jetbrains.buildServer.serverSide.SFinishedBuild
import jetbrains.buildServer.vcs.SVcsModification
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy
import org.jetbrains.teamcity.TestPrioritizationUtils.isTestPrioritizationEnabled
import org.jetbrains.teamcity.TestPrioritizationUtils.toRatioOrNull

class TestPrioritizationContextProcessor : BuildStartContextProcessor {
    private fun get(modification: SVcsModification, builds: Collection<SFinishedBuild>): SFinishedBuild? {
        return builds.find { build ->
            build.isFinished && build.revisions.any { it.revision == modification.version }
        } ?: modification.parentModifications.mapNotNull {
            get(it, builds)
        }.maxByOrNull {
            it.finishDate
        }
    }

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

        val lastChange = build.getChanges(SelectPrevBuildPolicy.SINCE_NULL_BUILD, true).first()
        val allBuilds = build.buildType?.getHistory(
            /* user = */ null,
            /* includeCanceled = */ false,
            /* orderByChanges = */ true
        ) ?: return

        val lastChangesBuild = get(lastChange, allBuilds) ?: return
        context.addSharedParameter(PrioritizationConstants.PREVIOUS_BUILD_KEY, lastChangesBuild.buildId.toString())
    }
}
