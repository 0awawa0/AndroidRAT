package server

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import helper.Logger
import helper.protocol.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.net.*
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ServerThread: Thread() {

    private var running = false

    private var lastKeepAliveSent = 0L

    private val listeners = ArrayList<ServerStateListener>()

    private val tag = "Server"
    private val dataDivider = ":|:"

    private val threadPool = Executors.newCachedThreadPool()

    private val socket = ServerSocket(33455)

    private var clients = HashMap<String, Client>()

    override fun run() {

        running = true
        while (running) {
            val client = socket.accept()

            threadPool.submit {
                Logger.log(tag, "Client connected: ${client.remoteSocketAddress}")
                while (client.isConnected) {
                    val output = client.getOutputStream()
                    val input = client.getInputStream().bufferedReader()

                    while (true) {
                        val line = input.readLine()
                        Logger.log(tag, "Received $line")
                        output.write("line\n".toByteArray())
                        Logger.log(tag, "Sent back")
                    }
                }
                Logger.log(tag, "Client disconnected: ${client.remoteSocketAddress}")
            }

            sleep(10)
        }
    }

    fun stopThread() { running = false }

    fun registerListener(listener: ServerStateListener) {
        if (listener !in listeners) listeners.add(listener)
    }

    fun unregisterListener(listener: ServerStateListener) { listeners.remove(listener) }

    fun requestPhoneInfo(client: Client) {

    }

    fun requestContacts(client: Client) {
        val msg = Message.builder().putData("command", "contacts").setToken(client.token).build()
        FirebaseMessaging.getInstance().send(msg)
    }

    fun requestLocation(client: Client) {

    }

    private fun answer(client: Client, packet: Packet) {

    }
}