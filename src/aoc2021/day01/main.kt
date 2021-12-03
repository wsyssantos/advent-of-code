package aoc2021.day01

import readIntInput

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readIntInput( 2021, 1,"Day01_test")
    check(calculatePartOne(testInput) == 7)
    check(calculatePartTwo(testInput) == 5)

    val input = readIntInput(2021, 1,"Day01")
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