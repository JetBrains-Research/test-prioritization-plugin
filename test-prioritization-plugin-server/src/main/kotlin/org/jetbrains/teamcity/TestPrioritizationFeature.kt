package org.jetbrains.teamcity

import jetbrains.buildServer.serverSide.BuildFeature
import jetbrains.buildServer.web.openapi.PluginDescriptor

class TestPrioritizationFeature(descriptor: PluginDescriptor) : BuildFeature() {
    private val editSettingsUrl = descriptor.getPluginResourcesPath("editSettings.jsp")

    override fun getType(): String = PrioritizationConstants.FEATURE_TYPE

    override fun getDisplayName(): String = PrioritizationConstants.DISPLAY_NAME

    override fun getEditParametersUrl(): String = editSettingsUrl

    override fun isMultipleFeaturesPerBuildTypeAllowed(): Boolean = false

    override fun describeParameters(params: MutableMap<String, String>): String {
        return "Tests folder: '${params[PrioritizationConstants.TESTS_FOLDER_KEY]}'"
    }
}
