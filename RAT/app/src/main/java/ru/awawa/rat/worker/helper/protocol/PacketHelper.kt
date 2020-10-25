package ru.awawa.rat.worker.helper.protocol

import ru.awawa.rat.worker.helper.toInt


class PacketHelper {
    companion object {
        fun create(buffer: ByteArray): Packet? {

            var packet: Packet? = null

            when (buffer.sliceArray(0..3).toInt()) {
                MagicNumber.START.value -> {
                    packet = StartPacket(String(buffer.sliceArray(4 until buffer.count())))
                }

                MagicNumber.PHONE_INFO.value -> {
                    val s = String(buffer.sliceArray(4 until buffer.count())).split('\n', limit = 1)
                    val id = s[0]
                    val info = s[1]
                    packet = PhoneInfoPacket(id, info)
                }
            }

            return packet
        }
    }
}