package ui.main

import helper.LogListener
import helper.Logger


class MainPresenter(private val view: MainView): LogListener {

    companion object {
        const val TAG = "MainPresenter"
    }

    override val id = Logger.listenerID

    override fun onMessageLogged(message: String) {
        view.putToLog(message)
    }

    init { Logger.registerListener(this) }

    fun onUndock() {
        Logger.unregisterListener(this)
        Logger.log(TAG, "Unregistered listener")
    }
}