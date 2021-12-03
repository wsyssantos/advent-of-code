import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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
