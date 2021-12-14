package aoc2021.day14

import kotlinx.coroutines.*
import readInput

fun main() = runBlocking {
    val (startTest, dictTest) = readInput(2021, 14, "test").sanitize()
    check(calculatePart1(startTest, dictTest, finalStep = 10) == 1588L)
    check(calculatePart2(startTest, dictTest) == 2188189693529L)

    val (start, dict) = readInput(2021, 14, "game").sanitize()
    println(calculatePart1(start, dict, 1, 10))
    println(calculatePart2(start, dict))
}

private fun calculatePart1(input: String, dict: Map<String, Char>, step: Int = 1, finalStep: Int) : Long =
    if (step <= finalStep) {
        buildString {
            var index = 0
            input.windowed(2).onEach {
                if (index++ == 0) append(it[0])
                append(dict[it])
                append(it[1])
            }
        }.let {
            calculatePart1(it, dict, step + 1, finalStep)
        }
    } else {
        input.groupBy { it }.map { it.value.size.toLong() }.let { count ->
            count.maxOf { it } - count.minOf { it }
        }
    }

private fun calculatePart2(input: String, dict: Map<String, Char>) : Long  {
    val totalCountMap = input.groupBy { it }.mapValues { it.value.size.toLong() }.toMutableMap()
    var pairs = input.windowed(2).associateWith { 1L }
    (1..40).onEach {
        pairs = buildMap {
            pairs.onEach { (key, value) ->
                val newChar = dict.getValue(key)
                this.addNewValue("${key[0]}$newChar", value)
                this.addNewValue("$newChar${key[1]}", value)
                totalCountMap.addNewValue(newChar, value)
            }
        }
    }
    return totalCountMap.maxOf { it.value } - totalCountMap.minOf { it.value }
}

private fun <T> MutableMap<T, Long>.addNewValue(newKey: T, value: Long) {
    val oldValue = this[newKey] ?: 0L
    this[newKey] = value + oldValue
}

private fun List<String>.sanitize() : Pair<String, Map<String, Char>> =
    this.filter { it.trim().isNotEmpty() }.partition { "->" in it }.let { (combinations, start) ->
        Pair(
            start.first(),
            buildMap {
                combinations.onEach {
                    val (key, value) = it.split(" -> ")
                    this[key] = value.toCharArray().first()
                }
            }
        )
    }
