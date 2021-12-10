package aoc2021.day10

import readInput
import java.util.*

fun main() {
    val testInput = readInput(2021, 10, "test")
    check(calculatePart1(testInput) == 26397)
    check(calculatePart2(testInput) == 288957L)

    val input = readInput(2021, 10, "game")
    println(calculatePart1(input))
    println(calculatePart2(input))
}

private fun calculatePart1(input: List<String>) : Int {
    val openChars = listOf('(', '[', '{', '<')
    val closeChars = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
    return  buildList {
        input.asSequence().forEach { chunk ->
            val openStack = Stack<Char>()
            chunk.forEach Chunks@ { char ->
                if (char in openChars) {
                    openStack.push(char)
                } else if (char in closeChars.keys) {
                    val lastOpened = openStack.pop()
                    if (!(lastOpened isPairOf char)) {
                        this.add(char)
                        return@Chunks
                    }
                }
            }
        }
    }.mapNotNull { char ->
        closeChars[char]
    }.sum()
}

private fun calculatePart2(input: List<String>) : Long {
    val chars = listOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
    val closeChars = mapOf(')' to 1L, ']' to 2L, '}' to 3L, '>' to 4L)
    return buildList {
        input.map { chunk ->
            val openStack = Stack<Char>()
            val closeStack = Stack<Char>()
            chunk.forEach Chunks@ { char ->
                val (open, close) = chars.first { it.first == char || it.second == char }
                when (char) {
                    open -> {
                        openStack.push(char)
                        closeStack.push(close)
                    }
                    close -> {
                        val closed = closeStack.pop()
                        if (char == closed) {
                            openStack.pop()
                        } else {
                            return@Chunks
                        }
                    }
                    else -> return@Chunks
                }
            }
            if (openStack.size == closeStack.size) {
                closeStack.reversed().fold(0L) { acc, c ->
                    (acc * 5L) + closeChars.getValue(c)
                }
            } else 0L
        }.filter { it != 0 }
        .also {
            this.addAll(it)
        }
    }
    .sortedDescending()
    .let { list ->
        val position = (list.size / 2)
        list[position]
    }
}

private infix fun Char.isPairOf(other: Char) : Boolean =
    this == '(' && other == ')' ||
    this == '[' && other == ']' ||
    this == '{' && other == '}' ||
    this == '<' && other == '>'
