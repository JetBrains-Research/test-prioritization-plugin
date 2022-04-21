package org.jetbrains.teamcity.testPrioritization

import org.junit.jupiter.api.ClassOrderer
import org.junit.jupiter.api.ClassOrdererContext
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.MethodOrdererContext
import java.lang.reflect.Method

const val TEST_PRIORITIZATION_CONFIG = "/test-prioritization-config.txt"

class CustomOrder : MethodOrderer, ClassOrderer {
    private val config = this::class.java.getResourceAsStream(TEST_PRIORITIZATION_CONFIG)

    private val successProbability = config?.bufferedReader()?.readLines()?.associate {
        it.substringBeforeLast(":") to it.substringAfterLast(":").toDouble()
    }

    private val Method.simpleName
        get() = "${declaringClass.name}.${name}"

    override fun orderMethods(context: MethodOrdererContext) {
        successProbability ?: return
        context.methodDescriptors.sortBy {
            successProbability.getOrDefault(it.method.simpleName, 0.0)
        }
    }

    override fun orderClasses(context: ClassOrdererContext) {
        successProbability ?: return
        context.classDescriptors.sortByDescending {
            it.testClass.declaredMethods.sumOf { method ->
                1.0 - successProbability.getOrDefault(method.simpleName, 0.0)
            }
        }
    }
}
