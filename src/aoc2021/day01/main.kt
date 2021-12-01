package aoc2021.day01

import readInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput( 2021, 1,"Day01_test").map { it.toInt() }
    check(calculatePartOne(testInput) == 7)
    check(calculatePartTwo(testInput) == 5)

    val input = readInput(2021, 1,"Day01").map { it.toInt() }
    println(calculatePartOne(input))
    println(calculatePartTwo(input))
}

fun calculatePartOne(input: List<Int>): Int {
    var sum = 0
    input.reduce { acc, value ->
        if (value > acc) sum++
        value
    }
    return sum
}

fun calculatePartTwo(input: List<Int>): Int =
    input.windowed(3).map {
        it.sum()
    }.let {
        calculatePartOne(it)
    }