package server

import java.net.InetAddress
import java.util.*

class Client(val id: String) {

    enum class State {
        DISCONNECTED,
        CONNECTED
    }
    constructor(): this(UUID.randomUUID().toString())

    var lastCheckIn: Long = 0
    var address: InetAddress? = null
    var port: Int = 0
    var state: State = State.DISCONNECTED

    fun setStateConnected() {
        state = State.CONNECTED
        lastCheckIn = Date().time
    }

    fun setStateDisconnected() {
        state = State.DISCONNECTED
        lastCheckIn = 0
    }
}