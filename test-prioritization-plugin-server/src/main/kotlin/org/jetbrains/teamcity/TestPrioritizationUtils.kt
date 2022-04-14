package org.jetbrains.teamcity

internal object TestPrioritizationUtils {
    fun String.toRatioOrNull(): Pair<Int, Int>? {
        val splitFraction = split("/").mapNotNull { it.toIntOrNull() }
        return if (splitFraction.size == 2) {
            splitFraction[0] to splitFraction[1]
        } else null
    }
}
