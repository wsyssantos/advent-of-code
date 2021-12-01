fun main() {
    fun calculate(input: List<Int>): Int {
        var sum = 0
        input.reduce { acc, value ->
            if (value > acc) sum++
            value
        }
        return sum
    }

    fun calculatePartTwo(input: List<Int>): Int {
        var sum = 0
        input.mapIndexed { index, _ ->
            val sublist = input.getValidSublist(index)
            if (sublist.all { it > 0 }.not()) {
                null
            } else {
                sublist.sum()
            }
        }.filterNotNull().reduce { acc, value ->
            if (value > acc) sum++
            value
        }
        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test").map { it.toInt() }
    check(calculate(testInput) == 7)
    check(calculatePartTwo(testInput) == 5)

    val input = readInput("Day01").map { it.toInt() }
    println(calculate(input))
    println(calculatePartTwo(input))
}

fun List<Int>.getValidSublist(index: Int) : List<Int> =
    listOf(
        this.getOrZero(index),
        this.getOrZero(index + 1),
        this.getOrZero(index + 2),
    )

fun List<Int>.getOrZero(index: Int) : Int {
    return if (index <= lastIndex) {
        this[index]
    } else 0
}