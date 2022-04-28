package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.BuildStartContext
import jetbrains.buildServer.serverSide.BuildStartContextProcessor
import jetbrains.buildServer.serverSide.SFinishedBuild
import jetbrains.buildServer.vcs.SVcsModification
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy
import org.jetbrains.teamcity.PrioritizationConstants.PREVIOUS_BUILD_KEY
import org.jetbrains.teamcity.TestPrioritizationUtils.isTestPrioritizationEnabled

class TestPrioritizationContextProcessor : BuildStartContextProcessor {
    private fun getLastFinishedBuild(modification: SVcsModification, builds: List<SFinishedBuild>): SFinishedBuild? {
        return builds.find { build ->
            build.isFinished && build.revisions.any { it.revision == modification.version }
        } ?: modification.parentModifications.mapNotNull {
            getLastFinishedBuild(it, builds)
        }.maxByOrNull {
            it.finishDate
        }
    }

    override fun updateParameters(context: BuildStartContext) {
        val build = context.build
        if (!build.isTestPrioritizationEnabled()) return

        val lastChange = build.getChanges(SelectPrevBuildPolicy.SINCE_NULL_BUILD, true).firstOrNull() ?: return
        val allBuilds = build.buildType?.getHistory(
            /* user = */ null,
            /* includeCanceled = */ false,
            /* orderByChanges = */ true
        ) ?: return

        val lastChangesBuild = getLastFinishedBuild(lastChange, allBuilds) ?: return
        context.addSharedParameter(PREVIOUS_BUILD_KEY, lastChangesBuild.buildId.toString())
    }
}
