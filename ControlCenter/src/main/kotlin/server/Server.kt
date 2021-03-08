package server

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import helper.Logger
import helper.protocol.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.net.*
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ServerThread: Thread() {

    private var running = false

    val listeners = ArrayList<ServerStateListener>()

    private val tag = "Server"

    private val threadPool = Executors.newCachedThreadPool()
    private val socket = ServerSocket(33455)

    val clients = ConcurrentHashMap<String, Client>()

    override fun run() {

        Logger.log(tag, "Server started")
        running = true
        while (running) {
            val client = socket.accept()
            threadPool.submit(ClientThread(WeakReference(this), client))
            sleep(10)
        }

        Logger.log(tag, "Server stopped")
    }

    fun stopThread() { running = false }

    fun registerListener(listener: ServerStateListener) {
        if (listener !in listeners) listeners.add(listener)
    }

    fun unregisterListener(listener: ServerStateListener) { listeners.remove(listener) }

    fun sendCommand(client: Client, command: String, additionalData: String = "") {
        val msg = Message.builder()
            .putData("command", command)
            .putData("additionalData", additionalData)
            .setToken(client.token)
            .build()
        FirebaseMessaging.getInstance().send(msg)
    }
}