package aoc2021.day04

import readInput
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.text.Position

fun main() {
    val (testRounds, testGames) = readInput(2021, 4,"test").sanitize()
    check(calculatePartOne(testRounds, testGames) == 4512)
    check(calculatePartTwo(testRounds, testGames) == 1924)

    val (rounds, games) = readInput(2021, 4,"game").sanitize()
    println(calculatePartOne(rounds, games))
    println(calculatePartTwo(rounds, games))
}

private fun calculatePartOne(rounds: List<Int>, games: List<Game>) : Int {
    rounds.forEach { round ->
        games.forEach { game ->
            game.checkGame(round)
            if (game.isWinner.get()) {
                return game.freePositionsSum() * round
            }
        }
    }
    return 0
}

private fun calculatePartTwo(rounds: List<Int>, games: List<Game>) : Int {
    var lastGame: Game? = null
    var lastRound = 1
    val checkedGames = mutableSetOf<Game>()
    rounds.forEach { round ->
        games.forEach { game ->
            game.checkGame(round)
            if (game.isWinner.get() && !checkedGames.contains(game)) {
                lastGame = game
                lastRound = round
                checkedGames.add(game)
            }
            if (checkedGames.size == games.size) {
                return (lastGame?.freePositionsSum() ?: 1) * lastRound
            }
        }
    }
    return 0
}

data class Game(private val board: List<List<Int>>) {
    val isWinner = AtomicBoolean(false)
    private val freePositions: MutableList<Point> =
        mutableListOf<Point>().apply {
            (0..board.lastIndex).forEach { x ->
                (0..board.lastIndex).forEach { y ->
                    add(Point(x, y))
                }
            }
        }

    fun checkGame(number: Int) {
        board.forEachIndexed { x, line ->
            line.forEachIndexed { y, i ->
                if (i == number) {
                    freePositions.remove(Point(x, y))
                    return@forEachIndexed
                }
            }
        }
        calculateWinner()
    }

    private fun calculateWinner() {
        (0..board.lastIndex).forEach { index ->
            isWinner.set(
                freePositions.count { it.x == index } == 0 ||
                freePositions.count { it.y == index } == 0
            )
            if (isWinner.get()) {
                return
            }
        }
    }

    fun freePositionsSum() = freePositions.sumOf { pos ->
        board[pos.x][pos.y]
    }

    data class Point(val x: Int, val y: Int)
}

private fun List<String>.sanitize() : Pair<List<Int>, List<Game>> {
    val rounds = this.first().split(",").map { it.toInt() }
    val games = this.drop(1).filterNotEmpty().windowed(5, step = 5)
        .map { line ->
            line.map { numbers ->
                numbers.split(" ").filterNotEmpty().map { it.trim().toInt() }
            }.let { boards ->
                Game(boards)
            }
        }
    return Pair(rounds, games)
}

private fun List<String>.filterNotEmpty() =
    this.filter { it.trim().isNotEmpty() }