package ru.awawa.rat.worker.helper

import java.net.InetAddress


class State {

    enum class ConnectionState {
        DISABLED, CONNECTING, CONNECTED
    }

    var connectionState: ConnectionState = ConnectionState.DISABLED
    private set

    var serverAddress: InetAddress? = null
    private set

    var serverPort: Int = 0
    private set

    var id: String = ""

    var running = false

    fun connect(address: InetAddress, port: Int) {
        this.serverAddress = address
        this.serverPort = port
        this.running = true
        this.connectionState = ConnectionState.CONNECTING
    }

    fun setStateConnected() {
        this.connectionState = ConnectionState.CONNECTED
    }
}