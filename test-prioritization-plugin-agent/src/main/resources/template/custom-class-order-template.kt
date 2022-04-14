package org.jetbrains.teamcity.testPrioritization

import org.junit.jupiter.api.ClassOrderer
import org.junit.jupiter.api.ClassOrdererContext

const val CLASS_CONFIG = "/test-prioritization-class-config.txt"

class CustomClassOrder : ClassOrderer {
    override fun orderClasses(context: ClassOrdererContext) {
        val config = this::class.java.getResourceAsStream(CLASS_CONFIG) ?: return
        val priority = config.bufferedReader().readLines().mapIndexed { i, name -> name to i }.toMap()
        context.classDescriptors.sortBy {
            priority.getOrDefault(it.testClass.name, -1)
        }
    }
}
