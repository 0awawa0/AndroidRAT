package server

import helper.Logger
import helper.protocol.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.net.SocketTimeoutException

class Server private constructor(): Thread() {

    private val socket = DatagramSocket(43584)
    private var running = false

    private val clients = HashMap<String, Client>()

    companion object {
        const val TAG = "Server"
        val instance: Server by lazy { Server() }
    }

    override fun run() {
        this.running = true

        val buffer = ByteArray(2048)
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
                    val client = if (id.isBlank()) { Client() }
                    else { Client(id) }

                    client.address = datagramPacket.address

                    client.port = datagramPacket.port
                    clients[client.id] = client

                    Logger.log(TAG, "Received start request")
                    this@Server.answer(client, StartPacket(client.id))
                }

                MagicNumber.PHONE_INFO -> {
                    val id = (packet as PhoneInfoPacket).id
                    val info = packet.info

                    Logger.log(TAG, "Received data from $id:\n$info")
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