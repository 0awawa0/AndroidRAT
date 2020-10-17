package ru.awawa.rat.worker.helper.protocol

import java.nio.charset.Charset

fun intToBytes(number: Int): ByteArray {
    val result = ByteArray(4)
    result[0] = (number.and(0xff)).toByte()
    result[1] = (number.shr(8).and(0xff)).toByte()
    result[2] = (number.shr(16).and(0xff)).toByte()
    result[3] = (number.shr(24).and(0xff)).toByte()
    return result
}

interface Packet {
    val magicNumber: Int
    val data: ByteArray
}

class StartPacket(id: String): Packet {

    override val magicNumber: Int = 0xffbbeedd.toInt()
    override val data = intToBytes(magicNumber) + id.toByteArray(Charset.forName("UTF-8"))
}