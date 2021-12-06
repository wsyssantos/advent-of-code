package aoc2021.day05

import readInput
import kotlin.math.max

fun main() {
    val testInput = readInput(2021, 5,"test").sanitize()
    check(calculatePart1(testInput) == 5)
    check(calculatePart2(testInput) == 12)

    val input = readInput(2021, 5,"game").sanitize()
    println(calculatePart1(input))
    println(calculatePart2(input))
}

private fun calculatePart1(input: List<Line>) : Int =
    input
        .verticalAndHorizontal()
        .getResult()

private fun calculatePart2(input: List<Line>) : Int =
    input
        .verticalHorizontalAndDiagonal()
        .getResult()

private fun List<Line>.verticalAndHorizontal() : List<Line> =
    this.filter { line ->
        line.startPoint.x == line.endPoint.x ||
        line.startPoint.y == line.endPoint.y
    }

private fun List<Line>.getResult() : Int =
    this.flatMap { it.lineDraw }
        .groupBy {
            it
        }.count {
            it.value.size >= 2
        }

private fun List<Line>.verticalHorizontalAndDiagonal() : List<Line> =
    this.filter { line ->
        line.run {
            startPoint.x == endPoint.x ||
            startPoint.y == endPoint.y ||
            ((startPoint.x range endPoint.x).stepCount() == 1 &&
            (startPoint.y range endPoint.y).stepCount() == 1)
        }
    }

private fun List<Int>.stepCount() : Int =
    this.windowed(2).map {
        it[1] - it[0]
    }.distinct().first().module()


private fun List<String>.sanitize() =
    this.map { line ->
        line.split(" -> ").let {
            val start = it.first().toPoint()
            val end = it[1].toPoint()
            Line(start, end)
        }
    }

data class Line(val startPoint: Point, val endPoint: Point) {
    val lineDraw = mutableListOf<Point>().apply {
        val xSteps = (startPoint.x range endPoint.x)
        val ySteps = (startPoint.y range endPoint.y)
        (0 until max(xSteps.count(), ySteps.count())).forEach { index ->
            val x = xSteps.getOrLastIndex(index)
            val y = ySteps.getOrLastIndex(index)
            add(Point(x, y))
        }
    }

    private fun List<Int>.getOrLastIndex(index: Int) =
        if (index < lastIndex) {
            this[index]
        } else {
            this[lastIndex]
        }
}

data class Point(val x: Int, val y: Int)

private fun String.toPoint() : Point =
    this.split(",").map { it.toInt() }.let { (x, y) -> Point(x,y) }

private infix fun Int.range(to: Int) : List<Int> =
    if (this <= to) (this .. to).toList()
    else (this downTo to).toList()

private fun Int.module() = if (this < 0) this * -1 else this

