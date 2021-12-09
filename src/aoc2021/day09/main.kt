package aoc2021.day09

import readInput

fun main() {
    val testInput = readInput(2021, 9, "test").sanitize()
    check(calculatePart1(testInput) == 15)
    check(calculatePart2(testInput) == 1134)

    val input = readInput(2021, 9, "game").sanitize()
    println(calculatePart1(input))
    println(calculatePart2(input))
}

private fun getLowPoints(input: List<List<Int>>) : List<LowPoint> =
    mutableListOf<LowPoint>().apply {
        input.asSequence().forEachIndexed { y, line ->
            line.asSequence().forEachIndexed { x, row ->
                val lowPoint = LowPoint(row, x, y)
                val adjacent = lowPoint.getAdjacentPoints(input)
                if (adjacent.all { it.value > lowPoint.value }) {
                    add(lowPoint)
                }
            }
        }
    }

private fun calculatePart1(input: List<List<Int>>) : Int =
    getLowPoints(input).sumOf { it.riskLevel }

private fun calculatePart2(input: List<List<Int>>) : Int =
    getLowPoints(input)
        .asSequence()
        .map { lowPoint ->
            mutableListOf<LowPoint>().apply {
                lowPoint
                    .getAdjacentPoints(input)
                    .forEach {
                        calculateBasin(input, it, this)
                    }
            }.size
        }
        .sortedDescending()
        .take(3)
        .reduce { acc, i ->
            acc * i
        }

private fun Int.toLowPoint(x: Int, y: Int) = LowPoint(this, x, y)

private fun LowPoint.getAdjacentPoints(input: List<List<Int>>) =
    this.run {
        val topPos = y - 1
        val bottomPos = y + 1
        val leftPos = x - 1
        val rightPos = x + 1
        val top = input.getOrNull(topPos)?.getOrNull(x)?.toLowPoint(x, topPos)
        val bottom = input.getOrNull(bottomPos)?.getOrNull(x)?.toLowPoint(x, bottomPos)
        val left = input.getOrNull(y)?.getOrNull(leftPos)?.toLowPoint(leftPos, y)
        val right = input.getOrNull(y)?.getOrNull(rightPos)?.toLowPoint(rightPos, y)
        listOfNotNull(top, bottom, right, left)
    }

private fun calculateBasin(input: List<List<Int>>, lowPoint: LowPoint, basinList: MutableList<LowPoint>)  {
    val adjacent = lowPoint.getAdjacentPoints(input)
    if (lowPoint.value != 9 && !basinList.contains(lowPoint)) {
        basinList.add(lowPoint)
        adjacent.forEach {
            calculateBasin(input, it, basinList)
        }
    }
}

data class LowPoint(val value: Int, val x: Int, val y: Int) {
    val riskLevel: Int
        get() = value + 1
}

private fun List<String>.sanitize() =
    this.map { line ->
        line.map {
            it.toString().toInt()
        }
    }