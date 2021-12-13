package aoc2021.day13

import readInput
import javax.swing.text.Position

fun main() {
    val (testInstructions, testPoints) = readInput(2021, 13, "test").sanitize()
    check(calculatePart1(testInstructions, testPoints) == 17)
    calculatePart2(testInstructions, testPoints).print()

    val (instructions, points) = readInput(2021, 13, "game").sanitize()
    print(calculatePart1(instructions, points))
    calculatePart2(instructions, points).print()
}

private fun calculatePart1(instructions: List<Instruction>, points: List<Point>) : Int =
    buildList {
        calculatePoints(instructions.first(), points)
    }.size

private fun calculatePart2(instructions: List<Instruction>, points: List<Point>) : List<Point> =
    if (instructions.isNotEmpty()) {
        val newList = buildList {
            instructions.first().also {
                calculatePoints(it, points)
            }
        }
        calculatePart2(instructions.drop(1), newList)
    } else {
        points
    }

private fun MutableList<Point>.calculatePoints(instruction: Instruction, points: List<Point>) {
    ((instruction.position - 1) downTo 0).forEachIndexed { index, currentPosition ->
        val mirrorPosition = instruction.position + index + 1
        val max = if (instruction.direction == "y") points.maxOf { it.x } else points.maxOf { it.y }
        (0 .. max).forEach { otherPosition ->
            if (
                points.containsElement(instruction.direction, currentPosition, otherPosition) ||
                points.containsElement(instruction.direction, mirrorPosition, otherPosition)
            ) {
                add(
                    if (instruction.direction == "y")
                        Point(otherPosition, currentPosition)
                    else
                        Point(currentPosition, otherPosition)
                )
            }
        }
    }
}

data class Instruction(val direction: String, val position: Int)

data class Point(val x: Int, val y: Int)

private fun List<Point>.print() {
    val maxX = this.maxOf { it.x }
    val maxY = this.maxOf { it.y }
    (0..maxY).forEach { inY ->
        (0..maxX).forEach { inX ->
            this.getValue(inX, inY).also { point ->
                print(if (point != null) "#" else ".")
            }
        }
        println()
    }
}

private fun List<Point>.containsElement(direction: String, position: Int, otherPosition: Int) =
    if (direction == "y") {
        this.count { it.y == position && it.x == otherPosition } > 0
    } else {
        this.count { it.x == position && it.y == otherPosition } > 0
    }

private fun List<Point>.getValue(x: Int, y: Int) : Point? =
    this.firstOrNull { it.x == x && it.y == y }

private fun List<String>.sanitize() : Pair<List<Instruction>, List<Point>> =
    this.filter { it.trim().isNotEmpty() }.partition { "," in it }.let { (points, instructions) ->
        Pair(
            instructions.map {
                it.replace("fold along ", "")
            }.map {
                it.split("=")
            }.map { (direction, position) ->
                Instruction(direction, position.toInt())
            },
            points.map {
                it.split(",")
            }.map { (x, y) ->
                Point(x.toInt(), y.toInt())
            }
        )
    }