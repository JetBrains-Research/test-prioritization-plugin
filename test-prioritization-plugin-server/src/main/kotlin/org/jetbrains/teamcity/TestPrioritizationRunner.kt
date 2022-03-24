package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.web.openapi.PluginDescriptor


class TestPrioritizationRunner(private val descriptor: PluginDescriptor, registry: RunTypeRegistry) : RunType() {
    init {
        registry.registerRunType(this)
    }

    override fun getType() = PrioritizationConstants.RUNNER_TYPE

    override fun getDisplayName() = "Test Prioritization Runner"

    override fun getDescription() = "This is Test Prioritization Runner!"

    override fun getRunnerPropertiesProcessor() = PropertiesProcessor { properties ->
        val invalidProperties: MutableList<InvalidProperty> = ArrayList()

        val folder = properties[PrioritizationConstants.TESTS_FOLDER_KEY]
        if (folder == null || folder.isEmpty()) {
            invalidProperties.add(InvalidProperty(PrioritizationConstants.TESTS_FOLDER_KEY, "Should not be empty"))
        }

        invalidProperties
    }

    override fun getEditRunnerParamsJspFilePath() = descriptor.getPluginResourcesPath("editParams.jsp")

    override fun getViewRunnerParamsJspFilePath() = descriptor.getPluginResourcesPath("viewParams.jsp")

    override fun getDefaultRunnerProperties() = mapOf(PrioritizationConstants.TESTS_FOLDER_KEY to "src/test")

    override fun describeParameters(parameters: Map<String, String>): String {
        return "Test folder: '${parameters[PrioritizationConstants.TESTS_FOLDER_KEY]}'"
    }
}
