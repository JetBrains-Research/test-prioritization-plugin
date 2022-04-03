package org.jetbrains.teamcity.testPrioritization

import org.junit.jupiter.api.ClassOrderer
import org.junit.jupiter.api.ClassOrdererContext

class CustomClassOrder : ClassOrderer {
    private val config = listOf<String>(
        /*INSERT_YOUR_CONFIG_HERE*/
    )

    private val priority = config.mapIndexed { i, name -> name to i }.toMap()

    override fun orderClasses(context: ClassOrdererContext) =
        context.classDescriptors.sortBy {
            priority.getOrDefault(it.testClass.name, -1)
        }
}
