package server

import helper.Logger
import helper.protocol.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Server private constructor(): Thread() {

    private val socket = DatagramSocket(43584)
    private var running = false

    private var lastKeepAliveSent = 0L

    private val clients = HashMap<String, Client>()

    private val listeners = ArrayList<ServerStateListener>()

    companion object {
        const val TAG = "Server"
        const val CONNECTION_TIMEOUT = 60000L
        const val KEEP_ALIVE_TIMER = 20000L
        val instance: Server by lazy { Server() }
    }

    override fun run() {
        this.running = true

        val buffer = ByteArray(BUFFER_SIZE)
        val rcvPacket = DatagramPacket(buffer, buffer.count())

        this.socket.soTimeout = 1

        Logger.log(TAG, "Server started")
        while (running) {
            try {
                socket.receive(rcvPacket)
                processRequest(rcvPacket)
                buffer.fill(0)
            } catch (ex: SocketTimeoutException) {
                val currDate = Date().time
                var stateChanged = false
                clients.forEach { (_, client) ->
                    if (currDate - client.lastCheckIn > CONNECTION_TIMEOUT) {
                        client.setStateDisconnected()
                        stateChanged = true
                    }
                }

                if (currDate - lastKeepAliveSent > KEEP_ALIVE_TIMER) {
                    clients.forEach { (_, client) ->
                        if (client.state == Client.State.CONNECTED) {
                            val data = KeepAlivePacket(client.id).data
                            val packet = DatagramPacket(data,
                                    data.size,
                                    client.address,
                                    client.port)
                            socket.send(packet)
                        }
                    }
                }
                
                if (stateChanged) {
                    listeners.forEach { it.onClientsListChanged(clients.values.toList()) }
                }
            }
        }
        Logger.log(TAG, "Server stopped")
    }

    fun stopThread() {
        this.running = false
    }

    fun registerListener(listener: ServerStateListener) {
        if (listener !in listeners) listeners.add(listener)
    }

    fun unregisterListener(listener: ServerStateListener) { listeners.remove(listener) }

    fun requestPhoneInfo(client: Client) {
        val packet = PhoneInfoPacket(client.id, "")
        if (client.address != null) {
            socket.send(DatagramPacket(packet.data, packet.data.size, client.address!!, client.port))
        }
    }

    fun requestContacts(client: Client) {
        val packet = ContactsPacket(client.id, "")
        if (client.address != null) {
            socket.send(DatagramPacket(packet.data, packet.data.size, client.address!!, client.port))
        }
    }

    fun requestLocation(client: Client) {
        val packet = LocationPacket(client.id, "")
        if (client.address != null) {
            socket.send(DatagramPacket(packet.data, packet.data.size, client.address!!, client.port))
        }
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
                    clients[client.id]?.setStateConnected()

                    listeners.forEach { it.onClientsListChanged(clients.values.toList()) }

                    Logger.log(TAG, "Received start request")
                    this@Server.answer(client, StartPacket(client.id))
                }

                MagicNumber.PHONE_INFO -> {
                    val id = (packet as PhoneInfoPacket).id
                    val info = packet.info

                    Logger.log(TAG, "Received data from $id:\n$info")
                }

                MagicNumber.KEEP_ALIVE -> {
                    val id = (packet as KeepAlivePacket).id

                    clients[id]?.lastCheckIn = Date().time
                    listeners.forEach { it.onClientsListChanged(clients.values.toList()) }
                }

                MagicNumber.CONTACTS -> {
                    val id = (packet as ContactsPacket).id
                    val contacts = packet.contacts

                    Logger.log(TAG, "Received contacts from $id:\n$contacts")
                }

                MagicNumber.LOCATION -> {
                    val id = (packet as LocationPacket).id
                    val location = packet.location

                    Logger.log(TAG, "Received location from: $id:\n$location")
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