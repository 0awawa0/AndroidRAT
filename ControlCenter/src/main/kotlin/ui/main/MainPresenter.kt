package ui.main

import helper.LogListener
import helper.Logger
import server.Client
import server.Server
import server.ServerStateListener


class MainPresenter(private val view: MainView): LogListener, ServerStateListener {

    companion object {
        const val TAG = "MainPresenter"
    }

    override val id = Logger.listenerID

    override fun onMessageLogged(message: String) {
        view.putToLog(message)
    }

    override fun onClientsListChanged(clients: List<Client>) {
        view.updateTable(clients)
    }

    init {
        Logger.registerListener(this)
        Server.instance.registerListener(this)
    }

    fun onUndock() {
        Logger.unregisterListener(this)
        Server.instance.unregisterListener(this)
        Logger.log(TAG, "Unregistered listener")
    }
}