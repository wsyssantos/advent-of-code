package aoc2021.day15

import getUniqueValuesFromPairs
import readInput
import java.util.*

fun main() {
    val testInput = readInput(2021, 15, "test").sanitize()
    check(calculatePart1(testInput.copyOf()) == 40)
    check(calculatePart2(testInput.copyOf()) == 315)

    val input = readInput(2021, 15, "game").sanitize()
    println(calculatePart1(input.copyOf()))
    println(calculatePart2(input.copyOf()))
}

private fun calculatePart2(input: Array<Point>): Int {
    val newInput = input.toMutableList()

    val stepX = (input.maxOf { it.x } + 1)
    val stepY = (input.maxOf { it.y } + 1)

    var startY = 0
    repeat(5) { step ->
        (startY..newInput.maxOf { it.y }).asSequence().forEach { y ->
            (0..input.maxOf { it.x }).asSequence().forEach { x ->
                var currentPoint = newInput.value(x, y)
                repeat(4) {
                    val newValue = if (currentPoint.value < 9) currentPoint.value + 1 else 1
                    val nextX = currentPoint.x + stepX
                    val nextY = currentPoint.y + stepY
                    newInput.add(Point(nextX, y, newValue))
                    if (step < 4) newInput.add(Point(x, nextY, newValue))
                    currentPoint = newInput.value(nextX, y)
                }
            }
        }
        startY += stepY
    }

    return calculatePart1(newInput.toTypedArray())
}

private fun calculatePart1(input: Array<Point>) : Int {
    val weights = mutableMapOf<Pair<Point, Point>, Int>()

    input.forEach { point ->
        point.getAdjacentPoints(input).forEach { adjacent ->
            weights.putIfAbsent(Pair(point, adjacent), adjacent.value)
        }
    }
    val start = input.getValue(0, 0)!!
    val end = input.getValue(input.maxOf { it.x }, input.maxOf { it.y })!!
    val shortestPathTree = dijkstra(Graph(weights), start, end)
    val shortestPath = shortestPath(shortestPathTree, start, end)
    return shortestPath.drop(1).sumOf {
        it.value
    }
}

private fun Point.getAdjacentPoints(input: Array<Point>) =
    this.run {
        val topPos = y - 1
        val bottomPos = y + 1
        val leftPos = x - 1
        val rightPos = x + 1
        val top = input.getValue(x, topPos)
        val bottom = input.getValue(x, bottomPos)
        val left = input.getValue(leftPos, y)
        val right = input.getValue(rightPos, y)
        listOfNotNull(bottom, top, right, left).toTypedArray()
    }

private fun List<Point>.value(x: Int, y: Int): Point =
    this.first { it.x == x && it.y == y }

private fun Array<Point>.getValue(x: Int, y: Int): Point? =
    this.find { it.x == x && it.y == y }

data class Point(val x: Int, val y: Int, val value: Int) {
    override fun toString(): String = value.toString()

    var delta: Int = Int.MAX_VALUE

    override fun equals(other: Any?): Boolean =
        other is Point &&
                this.x == other.x &&
                this.y == other.y

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

data class Graph<T>(
    val vertices: Set<T>,
    val edges : Map<T, Set<T>>,
    val weights: Map<Pair<T, T>, Int>
) {
    constructor(weights: Map<Pair<T, T>, Int>) : this(
        vertices = weights.keys.toList().getUniqueValuesFromPairs(),
        edges = weights.keys
            .groupBy { it.first }
            .mapValues { it.value.getUniqueValuesFromPairs { x -> x !== it.key } }
            .withDefault { emptySet() },
        weights = weights
    )
}

fun dijkstra(graph: Graph<Point>, start: Point, end: Point): Map<Point, Point?> {
    val s = mutableSetOf<Point>()
    val previous: MutableMap<Point, Point?> = graph.vertices.associateWith { null }.toMutableMap()

    val queueDelta = PriorityQueue<Point>(compareBy { it.delta })
    graph.vertices.forEach {
        if (it == start) {
            it.delta = 0
        }
        queueDelta.add(it)
    }

    while (true) {
        val v: Point = queueDelta.poll()

        if (v == end) break

        graph.edges.getValue(v).minus(s).forEach { neighbour ->
            val newPath = v.delta + graph.weights.getValue(Pair(v, neighbour))

            if (newPath < neighbour.delta) {
                queueDelta.first { it == neighbour }.also {
                    queueDelta.remove(it)
                    it.delta = newPath
                    queueDelta.add(it)
                }
                previous[neighbour] = v
            }
        }
        s.add(v)
    }
    return previous.toMap()
}

fun <T> shortestPath(shortestPathTree: Map<T, T?>, start: T, end: T): List<T> {
    fun pathTo(start: T, end: T): List<T> {
        if (shortestPathTree[end] == null) return listOf(end)
        return listOf(pathTo(start, shortestPathTree[end]!!), listOf(end)).flatten()
    }

    return pathTo(start, end)
}

private fun List<String>.sanitize() : Array<Point> =
    this.mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            Point(x, y, c.digitToInt())
        }
    }.flatten().toTypedArray()