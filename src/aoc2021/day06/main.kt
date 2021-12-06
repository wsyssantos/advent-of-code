package aoc2021.day06

import kotlinx.coroutines.*
import readInput
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val testInput = readInput(2021, 6,"test").sanitize()
//    check(calculatePart2(testInput, 18) == 26L)
//    check(calculatePart2(testInput, 80) == 5934L)
//    check(calculatePart2(testInput, 256) == 26984457539)
//    check(calculatePart2(testInput.asFlow(), 18) == 26L)
//    check(calculatePart2(testInput, 256) == 26984457539)

//    launch(Dispatchers.Default) {
//        check(calculatePart2(testInput.asSequence(), 1, 256) == 26984457539)
//    }
    measureTimeMillis {
        println(calculatePart2(listOf(1), 192))
    }.also {
        println("$it milliseconds")
    }

//    524288
//    4058 milliseconds

    val input = readInput(2021, 6,"game").sanitize()
//    print(calculatePart1(input, 80))
//    print(calculatePart2(listOf(1), 256))
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

private suspend fun calculatePart2(list: List<Int>, days: Int) : Long =
    list.map {
        awaitAsync { calculateSequenceAsync(it, maxDays = days) }
    }.awaitAll().sum()

private tailrec suspend fun calculateSequenceAsync(number: Int, day: Int = 1, maxDays: Int) : Long = withContext(Dispatchers.Unconfined) {
    if (day <= maxDays) {
        return@withContext if (number > 0) {
            calculateSequenceAsync(number - 1, day + 1, maxDays)
        } else {
            val first = calculateSequenceAsync(6, day + 1, maxDays)
            val second = calculateSequenceAsync(6, day + 1, maxDays)
            first + second
        }
    }
    return@withContext 1L
}

private fun <T> awaitAsync(block: suspend CoroutineScope.() -> T) : Deferred<T> =
    GlobalScope.async(Dispatchers.Unconfined, block = block)

private fun List<String>.sanitize() : List<Int> =
    this.first().split(",").map { it.toInt() }