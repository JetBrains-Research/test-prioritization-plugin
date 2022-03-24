package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.web.openapi.PluginDescriptor


class EchoRunner(private val descriptor: PluginDescriptor, registry: RunTypeRegistry) : RunType() {
    init {
        registry.registerRunType(this)
    }

    override fun getType() = Constants.RUNNER_TYPE

    override fun getDisplayName() = "Echo Runner"

    override fun getDescription() = "This is Echo Runner"

    override fun getRunnerPropertiesProcessor() = PropertiesProcessor { ArrayList() }

    override fun getEditRunnerParamsJspFilePath() = descriptor.getPluginResourcesPath("editParams.jsp")

    override fun getViewRunnerParamsJspFilePath() = descriptor.getPluginResourcesPath("viewParams.jsp")

    override fun getDefaultRunnerProperties() = HashMap<String, String>()
}
