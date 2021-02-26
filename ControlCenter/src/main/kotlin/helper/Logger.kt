package helper

import java.text.SimpleDateFormat
import java.util.*

interface LogListener {
    val id: Long
    fun onMessageLogged(message: String)
}

class Logger {

    companion object {
        var listenerID: Long = 0
        get() {
            field += 1
            return field
        }
        private set

        private val logListeners = ArrayList<LogListener>()

        fun registerListener(listener: LogListener) {
            for (l in logListeners) {
                if (listener.id == l.id) return
            }

            logListeners.add(listener)
        }

        fun unregisterListener(listener: LogListener) {
            logListeners.removeIf { it.id == listener.id }
        }

        fun log(tag: String, message: String) {
            val date = Date()
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
            val logMessage = "${formatter.format(date)}::[$tag] - $message"

            println(logMessage)
            logListeners.forEach { it.onMessageLogged(logMessage) }
        }
    }
}