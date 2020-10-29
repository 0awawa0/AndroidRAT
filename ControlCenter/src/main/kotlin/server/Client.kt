package server

import java.net.InetAddress
import java.util.*

class Client(val id: String) {

    enum class State {
        DISCONNETED,
        CONNETED
    }
    constructor(): this(UUID.randomUUID().toString())

    var lastCheckIn: Long = 0
    var address: InetAddress? = null
    var port: Int = 0
    var state: State = State.DISCONNETED

    fun setStateConnected() {
        state = State.CONNETED
        lastCheckIn = Date().time
    }

    fun setStateDisconnected() {
        state = State.DISCONNETED
        lastCheckIn = 0
    }
}