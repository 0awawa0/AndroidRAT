package server

import helper.Logger
import helper.protocol.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.*
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ServerThread: Thread() {

    private val socket = ServerSocketChannel.open()
    private var running = false

    private var lastKeepAliveSent = 0L

//    private val clients = HashMap<String, Client>()

    private val listeners = ArrayList<ServerStateListener>()

    companion object {
        const val TAG = "Server"
        const val CONNECTION_TIMEOUT = 60000L
        const val KEEP_ALIVE_TIMER = 20000L
    }

    private var clients = ArrayList<SocketChannel>()

    override fun run() {
        running = true

        socket.configureBlocking(false)
        socket.bind(InetSocketAddress(33455))
        socket.socket().reuseAddress = true

        Logger.log(TAG, "Server started")
        while (running) {
            val client = socket.accept()
            if (client != null) {
                Logger.log(TAG, "Received connection ${client.localAddress}")
                client.write(ByteBuffer.wrap("Hello!".toByteArray()))
                clients.add(client)
            }
        }
        Logger.log(TAG, "Server stopped")
        socket.close()
    }

    fun stopThread() { running = false }

    fun registerListener(listener: ServerStateListener) {
        if (listener !in listeners) listeners.add(listener)
    }

    fun unregisterListener(listener: ServerStateListener) { listeners.remove(listener) }

    fun requestPhoneInfo(client: Client) {

    }

    fun requestContacts(client: Client) {

    }

    fun requestLocation(client: Client) {

    }

    private fun answer(client: Client, packet: Packet) {

    }
}