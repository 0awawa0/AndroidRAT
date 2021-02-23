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
    private val selector = ActorSelectorManager(threadPool.asCoroutineDispatcher())
    private val tcpSocketBuilder = aSocket(selector).tcp()
    private val server = tcpSocketBuilder.bind("127.0.0.1", 33455)

    private var clients = HashMap<String, Client>()

    override fun run() {
        runBlocking {
            running = true

            Logger.log(tag, "Server started")
            while (running) {
                val client = server.accept()

                launch {
                    val input = client.openReadChannel()
                    val output = client.openWriteChannel(true)
                    try {
                        while (true) {
                            val line = input.readUTF8Line()
                            if (line?.isNotBlank() == true) {
                                val lineArray = line.split(dataDivider)

                                when (val command = lineArray[0].trim()) {
                                    "connect" -> {
                                        val token = lineArray.getOrElse(1) { "" }

                                        if (token.isBlank()) {
                                            output.writeStringUtf8("error${dataDivider}token required\n")
                                            continue
                                        }

                                        val uuid = if (token in clients.values) {
                                            clients.let {
                                                clients.forEach {
                                                    if (it.value.token == token) return@let it.key
                                                }
                                                var ret = UUID.randomUUID().toString()
                                                while (ret in clients.keys) ret = UUID.randomUUID().toString()
                                                return@let ret
                                            }
                                        } else {
                                            var ret = UUID.randomUUID().toString()
                                            while (ret in clients.keys) ret = UUID.randomUUID().toString()
                                            ret
                                        }

                                        clients[uuid] = Client(uuid, token)
                                        Logger.log(
                                            tag,
                                            "Device requested connect. Token received: $token. UUID dedicated: $uuid."
                                        )

                                        output.writeStringUtf8("ok$dataDivider$uuid\n")

                                        val clientList = clients.values.toList()
                                        listeners.forEach { it.onClientsListChanged(clientList) }
                                    }

                                    "contacts" -> {
                                        val uuid = lineArray.getOrElse(1) { "" }
                                        val contacts = lineArray.getOrElse(2) { "" }

                                        if (uuid.isBlank()) {
                                            output.writeStringUtf8("error${dataDivider}UUID required\n")
                                            continue
                                        }

                                        Logger.log(tag, "Received contacts from $uuid\nContacts: $contacts")

                                        output.writeStringUtf8("ok\n")
                                    }

                                    else -> { Logger.log(tag, "Received command: $command")}
                                }
                            } else {
                                delay(50)
                            }
                        }
                    } catch (ex: Throwable) {
                        ex.printStackTrace()
                        client.close()
                    }
                }
            }
            Logger.log(tag, "Server stopped")

            server.close()
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