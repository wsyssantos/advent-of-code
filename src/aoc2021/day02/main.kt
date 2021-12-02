package aoc2021.day02

import readInput

fun main() {
    val testInput = readInput( 2021, 2,"Day02_test").sanitize()
    check(calculatePartOne(testInput) == 150)
    check(calculatePartTwo(testInput) == 900)

    val input = readInput(2021, 2,"Day02").sanitize()
    println(calculatePartOne(input))
    println(calculatePartTwo(input))
}

fun calculatePartOne(directions: Sequence<SubmarineStep>) = calculateChallenge(directions, Submarine())

fun calculatePartTwo(directions: Sequence<SubmarineStep>) = calculateChallenge(directions, AimSubmarine())

fun calculateChallenge(directions: Sequence<SubmarineStep>, submarine: Submarine) : Int {
    directions.forEach {
        submarine.calculatePosition(it)
    }
    return submarine.finalResult()
}

fun List<String>.sanitize() : Sequence<SubmarineStep> =
    this.map { str ->
        str.split(" ").transformToDirection()
    }.asSequence()

fun List<String>.transformToDirection() : SubmarineStep {
    val direction = this.first()
    val value = this[1].toInt()

    val subDirection = when (direction) {
        "forward" -> SubmarineDirection.FORWARD
        "down" -> SubmarineDirection.DOWN
        "up" -> SubmarineDirection.UP
        else -> throw Exception()
    }
    return SubmarineStep(subDirection, value)
}

open class Submarine(
    protected var horizontalPosition: Int = 0,
    protected var verticalPosition: Int = 0
) {
    open fun calculatePosition(step: SubmarineStep) {
        when (step.directions) {
            SubmarineDirection.FORWARD -> horizontalPosition += step.value
            SubmarineDirection.UP -> verticalPosition -= step.value
            SubmarineDirection.DOWN -> verticalPosition += step.value
        }
    }

    fun finalResult() = (horizontalPosition * verticalPosition)
}

class AimSubmarine(
    private var aim: Int = 0
) : Submarine() {
    override fun calculatePosition(step: SubmarineStep) {
        when (step.directions) {
            SubmarineDirection.FORWARD -> {
                horizontalPosition += step.value
                verticalPosition += step.value * aim
            }
            SubmarineDirection.UP -> aim -= step.value
            SubmarineDirection.DOWN -> aim += step.value
        }
    }
}

enum class SubmarineDirection {
    FORWARD,
    UP,
    DOWN;
}

class SubmarineStep(
    val directions: SubmarineDirection,
    val value: Int
)