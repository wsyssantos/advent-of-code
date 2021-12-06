package aoc2021.day06

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import readInput
import java.util.stream.Stream
import kotlin.math.max
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val testInput = readInput(2021, 6,"test").sanitize()
//    check(calculatePart2(testInput.asSequence(), 18) == 26L)
//    check(calculatePart2(testInput.asSequence(), 80) == 5934L)
    check(calculatePart2(testInput.asSequence(), 256) == 26984457539)
//    check(calculatePart2(testInput.asFlow(), 18) == 26L)
//    check(calculatePart2(testInput, 256) == 26984457539)

//    launch(Dispatchers.Default) {
//        check(calculatePart2(testInput.asSequence(), 1, 256) == 26984457539)
//    }
//    val input = readInput(2021, 6,"game").sanitize()
//    print(calculatePart1(input, 80))
}

private fun calculatePart1(list: List<Int>, days: Int) : Int =
    list.toMutableList().apply {
        (1..days).forEach { _ ->
            val newFishCount = this.filter { it == 0 }.size
            this.forEachIndexed { index, i ->
                val newValue = i - 1
                this[index] = if (newValue >= 0) newValue else 6
            }
            (0 until newFishCount).forEach { _ ->
                this.add(8)
            }
        }
    }.size

private suspend fun calculatePart2(list: Sequence<Int>, days: Int) : Long =
    list.map {
        GlobalScope.async { calculateSequence(sequenceOf(it), maxDays = days) }
    }.sumOf {
        it.await()
    }

private tailrec suspend fun calculateSequence(list: Sequence<Int>, day: Int = 1, maxDays: Int) : Long {
    if (day <= maxDays) {
        val newFishCount = list.filter { it == 0 }.count()
        list.map {
            val newValue = it - 1
            if (newValue >= 0) newValue else 6
        }.let { newSequence ->
            val newStream = sequence {
                yieldAll(newSequence)
                (0 until newFishCount).forEach { _ ->
                    yield(8)
                }
            }
            return calculateSequence(newStream, day + 1, maxDays)
        }
    } else {
        return list.count().toLong()
    }
}


private fun List<String>.sanitize() : List<Int> =
    this.first().split(",").map { it.toInt() }