package ru.awawa.rat.helper

import android.content.Context
import androidx.core.content.edit

class Preferences private constructor(context: Context) {

    enum class PreferencesField {
        UUID,
        TOKEN,
        LAST_LOCATION,
        IP,
        PORT
    }

    private val sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    companion object {
        private const val FILENAME = "RAT_PREFERENCES"
        private var instance: Preferences? = null

        fun init(context: Context) { instance = Preferences(context) }

        @Suppress("UNCHECKED_CAST")
        fun <T>get(field: PreferencesField): T? {
            return try {
                when (field) {
                    PreferencesField.UUID -> {
                        instance?.sharedPreferences?.getString("UUID", "") as? T
                    }
                    PreferencesField.TOKEN -> {
                        instance?.sharedPreferences?.getString("TOKEN", "") as? T
                    }
                    PreferencesField.LAST_LOCATION -> {
                        instance?.sharedPreferences?.getString("LAST_LOCATION", "") as? T
                    }
                    PreferencesField.IP -> {
                        instance?.sharedPreferences?.getString("IP", "10.0.2.2") as? T
                    }
                    PreferencesField.PORT -> {
                        instance?.sharedPreferences?.getInt("PORT", 33455) as? T
                    }
                }
            } catch (ex: TypeCastException) {
                null
            }
        }

        fun <T>set(field: PreferencesField, value: T) {
            try {
                when (field) {
                    PreferencesField.UUID -> {
                        instance?.sharedPreferences?.edit{ putString(field.name, value.toString()) }
                    }
                    PreferencesField.TOKEN -> {
                        instance?.sharedPreferences?.edit { putString(field.name, value.toString()) }
                    }
                    PreferencesField.LAST_LOCATION -> {
                        instance?.sharedPreferences?.edit { putString(field.name, value.toString()) }
                    }
                    PreferencesField.IP -> {
                        instance?.sharedPreferences?.edit { putString(field.name, value.toString()) }
                    }
                    PreferencesField.PORT -> {
                        instance?.sharedPreferences?.edit { putInt(field.name, value as Int) }
                    }
                }
            } catch (ex: TypeCastException) {}
        }
    }
}