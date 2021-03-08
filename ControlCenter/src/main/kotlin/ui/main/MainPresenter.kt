package ui.main

import helper.LogListener
import helper.Logger
import server.Client
import server.ServerStateListener
import server.ServerThread
import tornadofx.runLater


class MainPresenter(private val view: MainView): LogListener, ServerStateListener {

    companion object {
        const val TAG = "MainPresenter"
    }

    override val id = Logger.listenerID

    override fun onMessageLogged(message: String) {
        runLater {
            view.putToLog(message)
        }
    }

    override fun onClientsListChanged(clients: List<Client>) {
        runLater {
            view.updateTable(clients)
        }
    }

    fun startServer() {
        Logger.registerListener(this)
        GlobalState.serverThread = ServerThread()
        GlobalState.serverThread?.registerListener(this)
        GlobalState.serverThread?.start()
    }

    fun stopServer() {
        GlobalState.serverThread?.unregisterListener(this)
        GlobalState.serverThread?.stopThread()
    }

    fun onUndock() {
        Logger.unregisterListener(this)
        GlobalState.serverThread?.unregisterListener(this)
        Logger.unregisterListener(this)
        Logger.log(TAG, "Unregistered listener")
    }
}