package server

import helper.protocol.MagicNumber
import helper.protocol.Packet
import helper.protocol.PacketHelper
import helper.protocol.StartPacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket

class Server: Thread() {

    private val socket = DatagramSocket()
    private var running = false

    private val clients = HashMap<String, Client>()

    override fun run() {
        this.running = true

        val buffer = ByteArray(256)
        val rcvPacket = DatagramPacket(buffer, buffer.count())

        while (running) {
            try {
                socket.receive(rcvPacket)
                processRequest(rcvPacket)
            } catch (ex: Exception) {

            }
        }
    }

    fun stopThread() {
        this.running = false
    }

    private fun processRequest(datagramPacket: DatagramPacket) {

        val packet = PacketHelper.create(datagramPacket.data) ?: return

        GlobalScope.launch {
            when (packet.magicNumber) {
                MagicNumber.START -> {
                    val id = (packet as StartPacket).id
                    val client = if (id.isEmpty()) Client() else Client(id)
                    client.address = datagramPacket.address
                    client.port = datagramPacket.port
                    clients[client.id] = client

                    this@Server.answer(client, StartPacket(id))
                }
            }
        }
    }

    private fun answer(client: Client, packet: Packet) {
        try {
            val datagram = DatagramPacket(packet.data, packet.data.count())
            datagram.address = client.address
            datagram.port = client.port
            this.socket.send(datagram)
        } catch (ex: Exception) {

        }
    }
}