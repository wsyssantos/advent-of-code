package aoc2021.day12

import readInput

fun main() {
    val testInput1 = readInput(2021, 12, "test1").sanitize()
    check(calculatePart1(testInput1) == 10)
    check(calculatePart2(testInput1) == 36)
    val testInput2 = readInput(2021, 12, "test2").sanitize()
    check(calculatePart1(testInput2) == 19)
    check(calculatePart2(testInput2) == 103)
    val testInput3 = readInput(2021, 12, "test3").sanitize()
    check(calculatePart1(testInput3) == 226)
    check(calculatePart2(testInput3) == 3509)

    val input = readInput(2021, 12, "game").sanitize()
    println(calculatePart1(input))
    println(calculatePart2(input))
}

private fun calculatePart1(path: Node) : Int =
    path.getCompletePaths(emptyList(), 0, Node::smallCavesOnlyOnce)

private fun calculatePart2(path: Node) : Int =
    path.getCompletePaths(emptyList(), 0, Node::smallCavesTwice)

private fun Node.getCompletePaths(
    previousSteps: List<Node>,
    prevSum: Int = 0,
    visitCheck: Node.(List<Node>) -> Boolean) : Int {
    var sum = prevSum
    if (value == "end") {
        sum++
    } else {
        this.children.asSequence().forEach {
            if (it.visitCheck(previousSteps + this)) {
                sum += it.getCompletePaths(previousSteps + this, 0, visitCheck)
            }
        }
    }
    return sum
}

private fun Node.smallCavesTwice(previousSteps: List<Node>) : Boolean {
    val previousLowerCase = previousSteps
        .filter {
            it.value.all { c -> c.isLowerCase() } &&
            it.value !in listOf("start", "end")
        }.groupBy {
            it.value
        }
    return if (this.value in previousLowerCase) {
        previousLowerCase.filter { it.value.size == 2 }.isEmpty()
    } else {
        this.value != "start"
    }
}

private fun Node.smallCavesOnlyOnce(previousSteps: List<Node>) : Boolean =
    if (this.value.all { it.isLowerCase() }) {
        this !in previousSteps
    } else {
        true
    }

data class Node(
    val value: String,
    val children: MutableSet<Node> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean =
        other is Node &&
        other.value == this.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value

    fun addChild(child: Node) {
        children.add(child)
    }
}

private fun Node.buildNodePath(set: Set<Node>, pairs: List<Pair<String, String>>) : Node =
    this.also { node ->
        pairs.filter { it.first == node.value }
            .map { pair ->
                set.first { it.value == pair.second }
            }.onEach {
                node.addChild(it)
            }

        pairs.filter { it.second == node.value }
            .map { pair ->
                set.first { it.value == pair.first }
            }.onEach {
                node.addChild(it)
            }
    }

private fun List<Pair<String, String>>.buildNodeSet() : Set<Node> =
    buildSet {
        val firsts = this@buildNodeSet.map { Node(it.first) }
        val seconds = this@buildNodeSet.map { Node(it.second) }
        addAll(firsts + seconds)
    }

private fun List<String>.sanitize() : Node =
    this.map {
        it.split("-")
    }.map {
        Pair(it[0], it[1])
    }.let { pairs ->
        pairs.buildNodeSet().let { set ->
            set.map {
                it.buildNodePath(set, pairs)
            }
        }
    }.first { it.value == "start" }