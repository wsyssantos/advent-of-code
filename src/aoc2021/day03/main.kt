package aoc2021.day03

import readInput

fun main() {
    val testInput = readInput(2021, 3,"Day03_test")
    check(calculatePartOne(testInput) == 198)
    check(calculatePartTwo(testInput) == 230)

    val input = readInput(2021, 3,"Day03")
    println(calculatePartOne(input))
    println(calculatePartTwo(input))
}

fun calculatePartOne(input: List<String>) : Int {
    var gammaRate = ""
    var epsilonRate = ""
    val childSize = input.first().length
    (0 until childSize).onEach { childPos ->
        var gamaRateCount = mutableMapOf<Int, Int?>(0 to 0, 1 to 0)
        input.map { child ->
            child[childPos].toString().toInt()
        }.forEach { bit ->
            gamaRateCount[bit] = gamaRateCount[bit]?.plus(1)
        }
        gammaRate += gamaRateCount.getKeyFrom(gamaRateCount.maxOf { it.value ?: 0 })
        epsilonRate += gamaRateCount.getKeyFrom(gamaRateCount.minOf { it.value ?: 0 })
        gamaRateCount = mutableMapOf(0 to 0, 1 to 0)
    }
    return gammaRate.bitToInt() * epsilonRate.bitToInt()
}

fun calculatePartTwo(input: List<String>) : Int {
    val oxygenRating = getLastRemainingItem(0, 1, input).first()
    val co2Rating = getLastRemainingItem(0, 0, input).first()
    return oxygenRating.bitToInt() * co2Rating.bitToInt()
}

fun getLastRemainingItem(index: Int, factor: Int, input: List<String>) : List<String> {
    if (input.size > 1) {
        val rateMap = mutableMapOf<Int, List<String>?>(0 to emptyList(), 1 to emptyList())
        input.forEach { child ->
            val bit = child[index].toString().toInt()
            rateMap[bit] = rateMap[bit]?.plus(child)
        }
        val zeroList = rateMap[0] ?: emptyList()
        val oneList = rateMap[1] ?: emptyList()
        return getLastRemainingItem(index + 1, factor, defineNextListByFactor(factor, oneList, zeroList))
    }
    return input
}

private fun defineNextListByFactor(factor: Int, oneList: List<String>, zeroList: List<String>) =
    if (factor == 1) {
        if (zeroList.size > oneList.size) {
            zeroList
        } else {
            oneList
        }
    } else {
        if (zeroList.size <= oneList.size) {
            zeroList
        } else {
            oneList
        }
    }

private fun String.bitToInt() = Integer.parseInt(this, 2)

private fun Map<Int, Any?>.getKeyFrom(target: Int) : String =
    this.filter { map ->
        map.value == target
    }.keys.first().toString()