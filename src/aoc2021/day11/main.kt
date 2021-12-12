package aoc2021.day11

import readInput

fun main() {
    val testInput = readInput(2021, 11, "test").sanitize()
    check(calculatePart1(testInput) == 1656L)
    val testInput2 = readInput(2021, 11, "test").sanitize()
    check(calculatePart2(testInput2) == 195)

    val input = readInput(2021, 11, "game").sanitize()
    println(calculatePart1(input))
    val input2 = readInput(2021, 11, "game").sanitize()
    println(calculatePart2(input2))
}

private fun calculatePart1(input: Array<Point>) : Long =
    (1 .. 100).sumOf {
        input.calculateHighlight()
    }

private fun calculatePart2(input: Array<Point>) : Int {
    var step = 1
    while(input.calculateHighlight() != 100L) { step ++ }
    return step
}

private fun Array<Point>.print() {
    (0..9).forEach { x ->
        (0..9).forEach { y ->
            print(this.getValue(x, y))
        }
        println()
    }
}

private fun Array<Point>.increment(checkHighlights: Boolean = false) {
    this.forEach {
        if (it.value != 10) {
            if (!checkHighlights || (checkHighlights && it.value != 0)) it.value++
        }
    }
}

private fun Array<Point>.calculateHighlight() : Long {
    var sum = 0L
    this.increment()
    while(this.any { it.value == 10 }) {
        this.filter { it.value == 10 }.forEach { point ->
            point.value = 0
            sum += 1
            point.getAdjacentPoints(this).apply {
                increment(true)
            }
        }
    }
    return sum
}

private fun Point.getAdjacentPoints(input: Array<Point>) =
    this.run {
        val topPos = y - 1
        val bottomPos = y + 1
        val leftPos = x - 1
        val rightPos = x + 1
        val top = input.getValue(x, topPos)
        val topRight = input.getValue(rightPos, topPos)
        val topLeft = input.getValue(leftPos, topPos)
        val bottom = input.getValue(x, bottomPos)
        val bottomRight = input.getValue(rightPos, bottomPos)
        val bottomLeft = input.getValue(leftPos, bottomPos)
        val left = input.getValue(leftPos, y)
        val right = input.getValue(rightPos, y)
        input.filter {
            it in listOf(
                top, topRight, topLeft,
                bottom, bottomRight, bottomLeft,
                right, left
            )
        }.toTypedArray()
    }

private fun Array<Point>.getValue(x: Int, y: Int): Point? =
    this.find { it.x == x && it.y == y }

data class Point(var value: Int, val x: Int, val y: Int) {

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean =
        other is Point &&
        this.x == other.x &&
        this.y == other.y

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

private fun List<String>.sanitize(): Array<Point> =
    this.mapIndexed { x, line ->
        line.mapIndexed { y, c ->
            Point(c.digitToInt(), x, y)
        }
    }.flatten().toTypedArray()