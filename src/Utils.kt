
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

/**
 * Reads lines from the given input txt file.
 */
fun readInput(year: Int, day: Int, name: String) =
    File("src/main/resources/aoc$year/day${day}", "$name.txt").readLines()

fun readIntInput(year: Int, day: Int, name: String) =
        readInput(year, day, name).map { it.toInt() }

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun Int.module() = if (this < 0) this * -1 else this

infix fun Int.range(to: Int) : List<Int> =
    if (this <= to) (this .. to).toList()
    else (this downTo to).toList()

fun <T> notNull(a: T?, b: T?, c: T?, d: T?, block: (T,T,T,T) -> Unit) {
    if (a != null && b != null && c != null && d != null) {
        block(a, b, c, d)
    }
}

fun String.getStr(index: Int) : String = this[index].toString()

inline fun measureTimeMillisAndPrint(block: () -> Unit) =
    measureTimeMillis(block).also {
        println("$it milli")
    }

fun <T> List<Pair<T, T>>.getUniqueValuesFromPairs(): Set<T> = this
    .map { (a, b) -> listOf(a, b) }
    .flatten()
    .toSet()

fun <T> List<Pair<T, T>>.getUniqueValuesFromPairs(predicate: (T) -> Boolean): Set<T> = this
    .map { (a, b) -> listOf(a, b) }
    .flatten()
    .filter(predicate)
    .toSet()


fun String.bitToInt() = this.toInt(2)

fun String.bitToLong() = this.toLong(2)