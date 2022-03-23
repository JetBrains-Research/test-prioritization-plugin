package com.jetbrains.testPrioritizationPlugin

import jetbrains.buildServer.serverSide.InvalidProperty
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

    override fun getDescription() = "This is my echo runner"

    override fun getRunnerPropertiesProcessor() = PropertiesProcessor { emptyList<InvalidProperty>() }

    override fun getEditRunnerParamsJspFilePath() = descriptor.getPluginResourcesPath("editParams.jsp")

    override fun getViewRunnerParamsJspFilePath() = descriptor.getPluginResourcesPath("viewParams.jsp")

    override fun getDefaultRunnerProperties() = emptyMap<String, String>()
}
