package org.jetbrains.teamcity

object PrioritizationConstants {
    private const val TEAMCITY_ARTIFACTS_DIR = ".teamcity"
    private const val ARTIFACTS_FOLDER_NAME = "test-prioritization"

    const val FEATURE_TYPE = "test-prioritization-type"
    const val DISPLAY_NAME = "Test Prioritization"
    const val TESTS_FOLDER_ROOT_PATHS_KEY = "prioritization-tests-folder-root-paths"
    const val PREVIOUS_BUILD_KEY = "test-prioritization-previous-build"
    const val ARTIFACT_CONFIG_NAME = "config.txt"
    const val ARTIFACT_FOLDER_PATH = "$TEAMCITY_ARTIFACTS_DIR/$ARTIFACTS_FOLDER_NAME"
    const val ARTIFACT_CONFIG_PATH = "$ARTIFACT_FOLDER_PATH/$ARTIFACT_CONFIG_NAME"
}
