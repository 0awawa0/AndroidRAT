package ru.awawa.rat.helper

import android.content.Context
import java.lang.Exception
import java.util.*

class Preferences private constructor(context: Context) {

    enum class PreferencesField {
        ID
    }

    private val sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    companion object {
        private const val FILENAME = "RAT_PREFERENCES"
        private var instance: Preferences? = null

        fun getInstance(context: Context?): Preferences? {
            if (instance == null) {
                if (context == null) return null
                instance = Preferences(context)
            }

            return instance
        }


        @Suppress("UNCHECKED_CAST")
        fun <T>get(field: PreferencesField): T? {
            return try {
                when (field) {
                    PreferencesField.ID -> {
                        instance?.sharedPreferences?.getString("ID", "") as? T
                    }
                }
            } catch (ex: TypeCastException) {
                null
            }
        }

        fun <T>set(field: PreferencesField, value: T) {
            try {
                when (field) {
                    PreferencesField.ID -> {
                        val editor = instance?.sharedPreferences?.edit()
                        editor?.putString(field.name, value as? String)
                        editor?.apply()
                    }
                }
            } catch (ex: TypeCastException) {}
        }
    }
}