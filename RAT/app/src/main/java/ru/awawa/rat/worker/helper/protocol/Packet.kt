package ru.awawa.rat.worker.helper.protocol

import ru.awawa.rat.worker.helper.toByteArray
import java.nio.charset.Charset


interface Packet {
    val magicNumber: MagicNumber
    val data: ByteArray
}

class StartPacket(val id: String): Packet {

    override val magicNumber = MagicNumber.START
    override val data: ByteArray = magicNumber.value.toByteArray() + "$id\n".toByteArray(Charset.forName("UTF-8"))
}

class PhoneInfoPacket(val id: String, val info: String): Packet {

    override val magicNumber = MagicNumber.PHONE_INFO
    override val data: ByteArray = magicNumber.value.toByteArray() + "$id\n$info".toByteArray(Charset.forName("UTF-8"))
}

class KeepAlivePacket(val id: String): Packet {

    override val magicNumber = MagicNumber.KEEP_ALIVE
    override val data: ByteArray = magicNumber.value.toByteArray() + "$id\n".toByteArray(Charset.forName("UTF-8"))
}