package server

import java.net.InetAddress
import java.util.*

class Client(val id: String) {

    constructor(): this(UUID.randomUUID().toString())

    var address: InetAddress? = null
    var port: Int = 0
}