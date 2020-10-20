package helper.protocol

import helper.toInt


class PacketHelper {
    companion object {
        fun create(buffer: ByteArray): Packet? {

            var packet: Packet? = null

            when (buffer.sliceArray(0..3).toInt()) {
                MagicNumber.START.value -> {
                    packet = StartPacket(String(buffer.sliceArray(4 until buffer.count())))
                }
            }

            return packet
        }
    }
}
