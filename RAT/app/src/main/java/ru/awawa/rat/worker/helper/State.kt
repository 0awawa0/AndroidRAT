package ru.awawa.rat.worker.helper

import java.net.InetAddress
import java.util.*


class State {

    companion object {
        private const val KEEP_ALIVE_INTERVAL = 20000L
        private const val CONNECTION_CHECK_INTERVAL = 60000L
    }

    enum class ConnectionState {
        DISABLED, CONNECTING, WAITING, CONNECTED
    }

    var connectionState: ConnectionState = ConnectionState.DISABLED
    private set

    var serverAddress: InetAddress? = null
    private set

    var serverPort: Int = 0
    private set

    var lastServerCheckIn: Long = 0L
    private set

    private var lastKeepAlive: Long = 0L
    private var lastStart: Long = 0L

    var id: String = ""

    var running = false

    fun connect(address: InetAddress, port: Int) {
        this.serverAddress = address
        this.serverPort = port
        this.running = true
        this.connectionState = ConnectionState.CONNECTING
    }

    fun setStateConnected() {
        this.lastKeepAlive = Date().time
        this.connectionState = ConnectionState.CONNECTED
    }

    fun setStateWaiting() {
        this.connectionState = ConnectionState.WAITING
    }

    fun keepAliveSent() { this.lastKeepAlive = Date().time }

    fun startSent() { this.lastStart = Date().time }

    fun needToSendKeepAlive(): Boolean {
        return Date().time - this.lastKeepAlive > KEEP_ALIVE_INTERVAL
                && this.connectionState == ConnectionState.CONNECTED
    }

    fun needToSendStartRequest(): Boolean {
        return Date().time - this.lastStart > CONNECTION_CHECK_INTERVAL
                && (this.connectionState == ConnectionState.CONNECTING || this.connectionState == ConnectionState.WAITING)
    }

    fun onKeepAliveReceived() {
        this.lastServerCheckIn = Date().time
    }
}