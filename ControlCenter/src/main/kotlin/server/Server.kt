package server

import helper.Logger
import helper.protocol.MagicNumber
import helper.protocol.Packet
import helper.protocol.PacketHelper
import helper.protocol.StartPacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.net.SocketTimeoutException

class Server private constructor(): Thread() {

    private val socket = DatagramSocket()
    private var running = false

    private val clients = HashMap<String, Client>()

    companion object {
        const val TAG = "Server"
        val instance: Server by lazy { Server() }
    }

    override fun run() {
        this.running = true

        val buffer = ByteArray(256)
        val rcvPacket = DatagramPacket(buffer, buffer.count())

        this.socket.soTimeout = 1

        Logger.log(TAG, "Server started")
        while (running) {
            try {
                socket.receive(rcvPacket)
                processRequest(rcvPacket)
            } catch (ex: SocketTimeoutException) {

            }
        }
        Logger.log(TAG, "Server stopped")
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