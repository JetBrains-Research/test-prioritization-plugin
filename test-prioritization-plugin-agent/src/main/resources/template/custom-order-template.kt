package org.jetbrains.teamcity.testPrioritization

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.MethodOrdererContext

const val METHOD_CONFIG = "/test-prioritization-method-config.txt"

class CustomOrder : MethodOrderer {
    override fun orderMethods(context: MethodOrdererContext) {
        val config = this::class.java.getResourceAsStream(METHOD_CONFIG) ?: return
        val priority = config.bufferedReader().readLines().mapIndexed { i, name -> name to i }.toMap()
        context.methodDescriptors.sortBy {
            val name = "${it.method.declaringClass.name}.${it.method.name}"
            priority.getOrDefault(name, -1)
        }
    }
}
