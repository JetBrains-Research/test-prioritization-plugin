package org.jetbrains.teamcity.testPrioritization

import org.junit.jupiter.api.ClassOrderer
import org.junit.jupiter.api.ClassOrdererContext
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.MethodOrdererContext
import java.lang.reflect.Method

const val TEST_PRIORITIZATION_CONFIG = "/test-prioritization-config.txt"

fun String.toRatioValue(): Double? {
    val splitFraction = split("/").mapNotNull { it.toIntOrNull() }
    return if (splitFraction.size == 2) {
        splitFraction[0].toDouble() / splitFraction[1]
    } else null
}

class CustomOrder : MethodOrderer, ClassOrderer {
    private val config = this::class.java.getResourceAsStream(TEST_PRIORITIZATION_CONFIG)

    private val successProbability = config?.bufferedReader()?.readLines()?.associate {
        it.substringBeforeLast(":") to it.substringAfterLast(":").toRatioValue()
    }

    private val Method.qualifiedName
        get() = "${declaringClass.name}.${name}"

    override fun orderMethods(context: MethodOrdererContext) {
        successProbability ?: return
        context.methodDescriptors.sortBy {
            successProbability.getOrDefault(it.method.qualifiedName, 0.0)
        }
    }

    override fun orderClasses(context: ClassOrdererContext) {
        successProbability ?: return
        context.classDescriptors.sortByDescending {
            it.testClass.declaredMethods.sumOf { method ->
                1.0 - (successProbability[method.qualifiedName] ?: 0.0)
            }
        }
    }
}
