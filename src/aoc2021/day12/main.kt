package aoc2021.day12

import readInput

fun main() {
    val testInput1 = readInput(2021, 12, "test1").sanitize()
    check(calculatePart1(testInput1) == 10)
//    val testInput2 = readInput(2021, 12, "test2").sanitize()
//    check(calculatePart1(testInput2) == 19)
//    val testInput3 = readInput(2021, 12, "test3").sanitize()
//    check(calculatePart1(testInput3) == 226)
}

fun calculatePart1(path: Node) : Int =
    path.getCompletePaths(emptyList(), 0)
        .also {
            println(it)
        }

private fun Node.getCompletePaths(previousSteps: List<Node>, prevSum: Int = 0) : Int {
    var sum = prevSum
    if (value == "end") {
        println((previousSteps + this).joinToString())
        sum++
    } else {
        this.children.forEach {
            sum += it.getCompletePaths(previousSteps + this, sum)
        }
    }
    return sum
}

private fun Node.canVisit(previousSteps: MutableList<Node>) =
    if (this.value.all { it.isLowerCase() }) {
        this !in previousSteps
    } else {
        true
    }

data class Node(val value: String, val children: MutableSet<Node> = mutableSetOf()) {
    override fun equals(other: Any?): Boolean =
        other is Node &&
        other.value == this.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value
}

private fun List<Pair<String, String>>.buildNodePath(baseNode: String) : Node =
    Node(baseNode).also { node ->
        filter { it.first == baseNode }.forEach { (_, dest) ->
            node.children.add(buildNodePath(dest))
        }
        filter { it.second == baseNode && it.first != "start" }.forEach { (ori, _) ->
            node.children.add(Node(ori))
        }
    }

private fun List<String>.sanitize() : Node =
    this.map {
        it.split("-")
    }.map {
        Pair(it[0], it[1])
    }.buildNodePath("start")