package aoc2021.day07

import module
import readInput

fun main() {
    val testInput = readInput(2021, 7,"test").sanitize()
    check(calculatePart1(testInput) == 37)
    check(calculatePart2(testInput) == 168)

    val input = readInput(2021, 7,"game").sanitize()
    println(calculatePart1(input))
    println(calculatePart2(input))
}

private fun calculatePart1(list: List<Int>) : Int =
    list.calculate { num, position -> (num - position).module() }

private fun calculatePart2(list: List<Int>) : Int =
    list.calculate { num, position -> progression(num, position) }

private fun List<Int>.calculate(fuelCalc: (Int, Int) -> Int) : Int {
    val max = maxOf { it }
    return mutableMapOf<Int, Int>().also { map ->
        (0..max).forEach { position ->
            val fuel = sumOf {
                fuelCalc(it, position)
            }
            map[position] = fuel
        }
    }.minOf { it.value }
}

private fun progression(from: Int, to: Int) : Int {
    val max = (from - to).module()
    return (1 .. max).sumOf { step ->
        step
    }
}

private fun List<String>.sanitize() : List<Int> =
    this.first().split(",").map { it.toInt() }
