package helper.protocol

import helper.toByteArray
import java.nio.charset.Charset

interface Packet {
    val magicNumber: MagicNumber
    val data: ByteArray
}

class StartPacket(val id: String): Packet {

    override val magicNumber = MagicNumber.START
    override val data: ByteArray = magicNumber.value.toByteArray() + id.toByteArray(Charset.forName("UTF-8"))
}

