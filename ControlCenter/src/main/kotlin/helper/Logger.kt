package helper

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class Logger {

    companion object {

        fun log(tag: String, message: String) {
            val date = Date()
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
            println("${formatter.format(date)}::[$tag]- $message")
        }
    }
}