package aoc2021.day16

import bitToInt
import bitToLong
import readInput

fun main() {
    val testInput1 = readInput(2021, 16, "test1").sanitize()
    check(calculatePart1(testInput1) == 16)
    val testInput2 = readInput(2021, 16, "test2").sanitize()
    check(calculatePart1(testInput2) == 12)
    val testInput3 = readInput(2021, 16, "test3").sanitize()
    check(calculatePart1(testInput3) == 23)
    val testInput4 = readInput(2021, 16, "test4").sanitize()
    check(calculatePart1(testInput4) == 31)
    listOf("C200B40A82").sanitize().also { check(calculatePart2(it) == 3L) }
    listOf("04005AC33890").sanitize().also { check(calculatePart2(it) == 54L) }
    listOf("CE00C43D881120").sanitize().also { check(calculatePart2(it) == 9L) }
    listOf("D8005AC2A8F0").sanitize().also { check(calculatePart2(it) == 1L) }
    listOf("F600BC2D8F").sanitize().also { check(calculatePart2(it) == 0L) }
    listOf("9C005AC2F8F0").sanitize().also { check(calculatePart2(it) == 0L) }
    listOf("9C0141080250320F1802104A08").sanitize().also { check(calculatePart2(it) == 1L) }

    val input = readInput(2021, 16, "game").sanitize()
    println(calculatePart1(input))
    println(calculatePart2(input))
}

private fun calculate(strInput: String) : List<Packet> =
    buildList {
        var input = strInput
        while (input.isValid()) {
            input = readPacket(this, input)
        }
    }

private fun calculatePart1(strInput: String) : Int =
    calculate(strInput).sumOf { it.sum() }

private fun calculatePart2(strInput: String) : Long =
    calculate(strInput).sumOf { it.evaluate() }

private fun readPacket(packetList: MutableList<Packet>, previousInput: String) : String {
    var input = previousInput
    val version = input.take(3).bitToInt()
    input = input.removeRange(0..2)
    val type = input.take(3).bitToInt()
    input = input.removeRange(0..2)

    if(type == 4) {
        var literalValue = ""
        var lastStart = input.first()
        while (true) {
            literalValue += input.take(5).drop(1)
            input = input.removeRange(0..4)
            if (lastStart == '0' || !input.isValid()) break
            lastStart = input.first()
        }
        packetList.add(LiteralValuePacket(version, type, literalValue))
    } else {
        val subPacketLength = input.take(1).toInt().let {
            if (it == 0) 15 else 11
        }
        input = input.removeRange(0 until 1)
        val nextSubPacketLength = input.take(subPacketLength).bitToInt()
        input = input.removeRange(0 until subPacketLength)

        if (subPacketLength == 11) {
            val newPacket = OperatorSubPacket(version, type, input)
            input = newPacket.generateSubPackets(nextSubPacketLength)
            packetList.add(newPacket)
        } else {
            val newPacket = OperatorPacket(version, type, input)
            input = newPacket.generateSubPackets(nextSubPacketLength)
            packetList.add(newPacket)
        }
    }
    return input
}

abstract class Packet(private val version: Int, private val typeId: Int, protected val rawValue: String) {
    val subPacketList = mutableListOf<Packet>()
    var value: Long? = null
    fun sum() : Int = version + subPacketList.sumOf { it.sum() }

    fun evaluate() : Long =
        when(typeId) {
            4 -> value!!
            0 -> subPacketList.sumOf { it.evaluate() }
            1 -> subPacketList.fold(1) { acc, packet ->
                acc * packet.evaluate()
            }
            2 -> subPacketList.minOf { it.evaluate() }
            3 -> subPacketList.maxOf { it.evaluate() }
            5 -> if (subPacketList[0].evaluate() > subPacketList[1].evaluate()) 1 else 0
            6 -> if (subPacketList[0].evaluate() < subPacketList[1].evaluate()) 1 else 0
            7 -> if (subPacketList[0].evaluate() == subPacketList[1].evaluate()) 1 else 0
            else -> error("error")
        }
}

class LiteralValuePacket(version: Int, typeId: Int, rawValue: String) : Packet(version, typeId, rawValue) {
    init {
        this.value = rawValue.bitToLong()
    }
}

class OperatorPacket(version: Int, typeId: Int, rawValue: String) : Packet(version, typeId, rawValue) {
    fun generateSubPackets(length: Int) : String {
        var input = rawValue.take(length)
        while(input.isValid()) {
            input = readPacket(subPacketList, input)
        }
        return rawValue.removeRange(0 until length)
    }
}

class OperatorSubPacket(version: Int, typeId: Int, rawValue: String) : Packet(version, typeId, rawValue) {
    fun generateSubPackets(packetNumber: Int) : String {
        var input = rawValue
        var count = 0
        while(count < packetNumber) {
            input = readPacket(subPacketList, input)
            count++
        }
        return input
    }
}

private fun String.isValid() = this.isNotEmpty() && !this.all { it == '0' }

private fun List<String>.sanitize() : String =
    this.first().toCharArray().joinToString("") {
        val bitRep = it.toString()
            .toBigInteger(16)
            .toString(2)
            .padStart(4, '0')
        bitRep
    }