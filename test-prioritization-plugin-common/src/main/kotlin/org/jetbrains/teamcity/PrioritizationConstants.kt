package org.jetbrains.teamcity

object PrioritizationConstants {
    const val FEATURE_NAME = "test-prioritization"
    const val FEATURE_TYPE = "test-prioritization-type"
    const val DISPLAY_NAME = "Test Prioritization Feature"
    const val TESTS_FOLDER_KEY = "prioritization-tests-folder"
    const val TEST_SORTED_CONFIG_KEY = "test-prioritization-custom-order"
    const val INSERT_YOUR_CONFIG_HERE = "/*INSERT_YOUR_CONFIG_HERE*/"
    const val WTF = "package org.jetbrains.teamcity.testPrioritization\n" +
            "\n" +
            "import org.junit.jupiter.api.MethodOrderer\n" +
            "import org.junit.jupiter.api.MethodOrdererContext\n" +
            "\n" +
            "class CustomOrder : MethodOrderer {\n" +
            "    private val config = listOf<String>(\n" +
            "        /*INSERT_YOUR_CONFIG_HERE*/\n" +
            "    )\n" +
            "\n" +
            "    private val priority = config.mapIndexed { i, name -> name to i }.toMap()\n" +
            "\n" +
            "    override fun orderMethods(context: MethodOrdererContext) =\n" +
            "        context.methodDescriptors.sortBy {\n" +
            "            val name = \"\${it.method.declaringClass.name}.\${it.method.name}\"\n" +
            "            priority.getOrDefault(name, -1)\n" +
            "        }\n" +
            "}\n"
    const val WTF2 = "junit.jupiter.testmethod.order.default=org.jetbrains.teamcity.testPrioritization.CustomOrder\n"
}
