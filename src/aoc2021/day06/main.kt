package aoc2021.day06

import readInput
import kotlin.system.measureTimeMillis


fun main() {
    val testInput = readInput(2021, 6,"test").sanitize()
    check(calculatePart1(testInput, 18) == 26)
    check(calculatePart1(testInput, 80) == 5934)
    check(calculatePart2(testInput, 256) == 26984457539L)

    val input = readInput(2021, 6,"game").sanitize()
    measureTimeMillis {
        println(calculatePart1(input, 80))
        println(calculatePart2(input, 256))
    }.also {
        println("$it milli")
    }

}

private fun calculatePart1(list: List<Int>, days: Int) : Int =
    list.toMutableList().apply {
        (1..days).forEach { _ ->
            val newFishCount = this.filter { it == 0 }.size
            this.forEachIndexed { index, i ->
                val newValue = i - 1
                this[index] = if (newValue >= 0) newValue else 6
            }
            (0 until newFishCount).forEach { _ ->
                this.add(8)
            }
        }
    }.size

private fun calculatePart2(list: List<Int>, days: Int) : Long {
    var agingGroup = list.groupBy { it }.mapValues { (_, value) ->
        value.size.toLong()
    }
    (1..days).forEach { _ ->
        val iterator = agingGroup.toSortedMap(Comparator.reverseOrder()).iterator()
        agingGroup = mutableMapOf<Int, Long>().apply {
            iterator.forEach { (key, value) ->
                if (key == 0) {
                    val nextValue = getOrDefault(8, 0)
                    remove(8)
                    putIfAbsent(8, nextValue + value)
                    val renewValue = getOrDefault(6, 0)
                    remove(6)
                    putIfAbsent(6, renewValue + value)
                } else {
                    val nextKey = key - 1
                    putIfAbsent(nextKey, value)
                }
            }
        }
    }
    return agingGroup.values.sumOf { it }
}

private fun List<String>.sanitize() : List<Int> =
    this.first().split(",").map { it.toInt() }