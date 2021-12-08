package aoc2021.day08

import readInput

fun main() {
    val testInput = readInput(2021, 8, "test").sanitize()
    check(calculatePart1(testInput) == 26)
    check(calculatePart2(testInput) == 61229)

    val input = readInput(2021, 8, "game").sanitize()
    println(calculatePart1(input))
    println(calculatePart2(input))
}

private fun calculatePart1(input: List<Pair<List<String>, List<String>>>) : Int =
    input.flatMap { (_, outputs) ->
        outputs
    }.filter {
        it.length in listOf(2, 4, 3, 7)
    }.size

private fun calculatePart2(input: List<Pair<List<String>, List<String>>>) : Int =
    input.sumOf { (inputs, outputs) ->
        val digitMap = inputs.createDigitMap()
        outputs.joinToString(separator = "") { output ->
            digitMap.getKey(output)
        }.toInt()
    }

private fun List<String>.createDigitMap() : Map<String, String> =
    mutableMapOf<String, String>().also {
        it.buildEasyNumbers(this)
        it.buildSixLengthNumbers(this)
        it.buildFiveLengthNumbers(this)
    }

private fun MutableMap<String, String>.buildEasyNumbers(inputs: List<String>) {
    val numOne = inputs.find { it.length == 2 }!!
    val numSeven = inputs.find { it.length == 3 }!!
    val numFour = inputs.find { it.length == 4 }!!
    val numEight = inputs.find { it.length == 7 }!!
    this["1"] = numOne
    this["4"] = numFour
    this["7"] = numSeven
    this["8"] = numEight
}

private fun MutableMap<String, String>.buildSixLengthNumbers(inputs: List<String>) {
    inputs.filter { it.length == 6 }.also { list ->
        this["9"] = list.first {
            this["4"]!!.all { char -> char in it }
        }
        this["6"] = list.first {
            this["7"]!!.elementsNotIn(it).length == 1
        }
        this["0"] = list.first {
            it !in this.values
        }
    }
}

private fun MutableMap<String, String>.buildFiveLengthNumbers(inputs: List<String>) {
    inputs.filter { it.length == 5 }.also { list ->
        this["3"] = list.first {
            this["7"]!!.all { char -> char in it }
        }
        this["5"] = list.first {
            this["6"]!!.elementsNotIn(it).length == 1
        }
        this["2"] = list.first {
            it !in this.values
        }
    }
}

private fun String.elementsNotIn(other: String) : String =
    this.filter {
        it !in other
    }

private fun Map<String, String>.getKey(value: String) =
    this.filter { it.value == value }.keys.first()

private fun String.sort() = String(toCharArray().apply { sort() })

private fun List<String>.sort() = this.map { it.sort() }

private fun List<String>.sanitize() : List<Pair<List<String>, List<String>>> =
    this.map {
        val list = it.split(" | ")
        val inputs = list[0]
        val outputs = list[1]
        Pair(inputs.split(" ").sort(), outputs.split(" ").sort())
    }

