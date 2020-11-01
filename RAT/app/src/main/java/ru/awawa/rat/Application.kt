package ru.awawa.rat

import android.app.Application
import ru.awawa.rat.helper.Preferences

class Application: Application() {

    override fun onCreate() {
        super.onCreate()

        Preferences.init(this)
    }
}