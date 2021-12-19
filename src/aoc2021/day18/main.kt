package aoc2021.day18

import readInput
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    val testInput1 = readInput(2021, 18, "test1").toSnailNumber()
    check(calculatePart1(testInput1).value == "[[[[1,1],[2,2]],[3,3]],[4,4]]")

    val testInput2 = readInput(2021, 18, "test2").toSnailNumber()
    check(calculatePart1(testInput2).value == "[[[[3,0],[5,3]],[4,4]],[5,5]]")

    val testInput3 = readInput(2021, 18, "test3").toSnailNumber()
    check(calculatePart1(testInput3).value == "[[[[5,0],[7,4]],[5,5]],[6,6]]")

    val testInput4 = readInput(2021, 18, "test4").toSnailNumber()
    check(calculatePart1(testInput4).value == "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")

    val testInput5 = readInput(2021, 18, "test5").toSnailNumber()
    check(calculatePart1(testInput5).value == "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]")
    check(calculatePart1(testInput5).magnitude() == 4140L)
    check(calculatePart2(testInput5) == 3993L)

    SnailNumber("[[1,2],[[3,4],5]]").apply { check(magnitude() == 143L) }
    SnailNumber("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").apply { check(magnitude() == 1384L) }
    SnailNumber("[[[[1,1],[2,2]],[3,3]],[4,4]]").apply { check(magnitude() == 445L) }
    SnailNumber("[[[[3,0],[5,3]],[4,4]],[5,5]]").apply { check(magnitude() == 791L) }
    SnailNumber("[[[[5,0],[7,4]],[5,5]],[6,6]]").apply { check(magnitude() == 1137L) }
    SnailNumber("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").apply { check(magnitude() == 3488L) }

    val input = readInput(2021, 18, "game").toSnailNumber()
    println(calculatePart1(input).magnitude())
    println(calculatePart2(input))
}

private fun calculatePart1(input: List<SnailNumber>) : SnailNumber =
    input.reduce { acc, snailNumber ->
        SnailNumber("[$acc,$snailNumber]").apply {
            reduce()
        }
    }

private fun calculatePart2(input: List<SnailNumber>) : Long =
    input.map { current ->
        input.filter { it != current }.map { other ->
            SnailNumber("[$current,$other]").run {
                reduce()
                magnitude()
            }
        }
    }.flatten().maxOf { it }

class SnailNumber(var value: String) {
    fun reduce() {
        var canExplode = canExplode()
        while (canExplode != null) {
            explode(canExplode)
            canExplode = canExplode()
        }

        var canSplit = canSplit()
        while (canSplit != null) {
            split(canSplit)
            canExplode = canExplode()
            while (canExplode != null) {
                explode(canExplode)
                canExplode = canExplode()
            }
            canSplit = canSplit()
        }
    }

    private fun split(splitMatch: MatchResult) {
        val numValue = splitMatch.value.toFloat()
        val newLeft = floor(numValue / 2).toInt()
        val newRight = ceil(numValue / 2).toInt()
        value = value.replaceRange(splitMatch.range, "[$newLeft,$newRight]")
    }

    private fun explode(indexToExplode: Int) {
        val regex = "\\[\\d+\\,\\d+\\]".toRegex()
        regex.find(value, startIndex = indexToExplode)?.also {
            val numberOnTheLeft = value.findFirstNumberOnTheLeft(indexToExplode)
            val (left, right) = it.value.clearPair()
            numberOnTheLeft?.also { (range, num) ->
                val newNum = left + num
                value = value.replaceRange(range, newNum.toString())
            }
            val numberOnTheRight = value.findFirstNumberOnTheRight(it.range.last + 1)
            numberOnTheRight?.also { (range, num) ->
                val newNum = right + num
                value = value.replaceRange(range, newNum.toString())
            }
        }
        regex.find(value, startIndex = indexToExplode)?.also {
            value = value.replaceRange(it.range, "0")
        }
    }

    private fun canExplode() : Int? {
        val stack = Stack<Char>()
        value.forEachIndexed { index, it ->
            if (it == '[') {
                stack.add(it)
            } else if (it == ']') {
                if (stack.isNotEmpty()) stack.pop()
            }
            if (stack.size == 5) {
                return index
            }
        }
        return null
    }

    private fun canSplit() : MatchResult? {
        val regex = "[\\d]+".toRegex()
        val matches = regex
            .findAll(value)
            .filter { it.value.toInt() >= 10 }
            .toList()
        return if (matches.isNotEmpty()) matches.first() else null
    }

    fun magnitude() : Long {
        val regex = "\\[\\d+\\,\\d+\\]".toRegex()
        var matches = regex.findAll(value).toList()
        while (matches.isNotEmpty()) {
            matches.forEach {
                val (left, right) = it.value.clearPair()
                val sum = (left * 3L) + (right * 2L)
                value = value.replace(it.value, "$sum")
            }
            matches = regex.findAll(value).toList()
        }
        return value.toLong()
    }

    override fun toString(): String = value
}

private fun String.findFirstNumberOnTheLeft(startIndex: Int) : Pair<IntRange, Int>? {
    val regex = "[\\d]+".toRegex()
    this.substring(0, startIndex).also { subs ->
        val matches = regex.findAll(subs)
        if (matches.toList().isNotEmpty()) {
            matches.last().also {
                return Pair(it.range, it.value.toInt())
            }
        } else {
            return null
        }
    }
}

private fun String.findFirstNumberOnTheRight(startIndex: Int) : Pair<IntRange, Int>? {
    val regex = "[\\d]+".toRegex()
    regex.find(this, startIndex = startIndex)?.also {
        return Pair(it.range, it.value.toInt())
    }
    return null
}

private fun String.clearPair() : Pair<Int, Int> {
    val clean = this.replace("[\\[\\]]".toRegex(), "")
    val split = clean.split(",")
    return split.map { it.toInt() }.let {
        Pair(it[0], it[1])
    }
}

private fun List<String>.toSnailNumber() : List<SnailNumber> =
    this.map { SnailNumber(it) }
