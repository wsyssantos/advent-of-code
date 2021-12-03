package aoc2021.day03

import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2021, 3,"Day03_test")
    check(calculatePartOne(testInput) == 198)
    check(calculatePartTwo(testInput) == 230)

    val input = readInput(2021, 3,"Day03")
    println(calculatePartOne(input))
//    println(calculatePartTwo(input))
}

fun calculatePartOne(input: List<String>) : Int {
    var gammaRate = ""
    var epsilonRate = ""
    var gamaRateCount = mutableMapOf<Int, Int?>(0 to 0, 1 to 0)
    val childSize = input.first().length
    (0 until childSize).onEach { childPos ->
        input.forEach { child ->
            val bit = child[childPos].toString().toInt()
            gamaRateCount[bit] = gamaRateCount[bit]?.plus(1)
        }
        gammaRate += gamaRateCount.getKeyFrom(gamaRateCount.maxOf { it.value ?: 0 })
        epsilonRate += gamaRateCount.getKeyFrom(gamaRateCount.minOf { it.value ?: 0 })
        gamaRateCount = mutableMapOf(0 to 0, 1 to 0)
    }
    return Integer.parseInt(gammaRate, 2) * Integer.parseInt(epsilonRate, 2)
}

fun calculatePartTwo(input: List<String>) : Int {
    if (input.size != 1) {
        var gamaRateCount = mutableMapOf<Int, Int?>(0 to 0, 1 to 0)
        val childSize = input.first().length
        (0 until childSize).onEach { childPos ->
            input.forEach { child ->
                val bit = child[childPos].toString().toInt()
                gamaRateCount[bit] = gamaRateCount[bit]?.plus(1)
            }
        }
    } else {

    }
}

private fun Map<Int, Int?>.getKeyFrom(target: Int) : String =
    this.filter { map ->
        map.value == target
    }.keys.first().toString()