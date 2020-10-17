package ru.awawa.rat.worker.helper

import java.net.InetAddress


class State {

    enum class ConnectionState {
        DISABLED, CONNECTING, CONNECTED
    }

    var connectionState: ConnectionState = ConnectionState.DISABLED
    private set

    var connectionAddress: InetAddress? = null
    private set

    var running = false

    fun connect(address: InetAddress) {
        this.connectionAddress = address
        this.running = true
        this.connectionState = ConnectionState.CONNECTING
    }
}