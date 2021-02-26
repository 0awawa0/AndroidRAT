package ui.main

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import server.Client
import tornadofx.*
import java.text.SimpleDateFormat
import java.util.*


class MainView: View("ControlCenter") {

    private val presenter = MainPresenter(this)

    private val btStart = button {
        text = "Start server"
        font = Font(14.0)
        borderpaneConstraints {
            alignment = Pos.CENTER
        }
    }

    private val btStop = button {
        text = "Stop server"
        font = Font(14.0)
        borderpaneConstraints {
            alignment = Pos.CENTER
        }
    }

    private val btPhoneInfo = button {
        text = "Get phone info"
        font = Font(14.0)
        borderpaneConstraints {
            alignment = Pos.CENTER
        }
    }

    private val btContacts = button {
        text = "Get contacts"
        font = Font(14.0)
        borderpaneConstraints { alignment = Pos.CENTER }
    }

    private val btLocation = button {
        text = "Get location"
        font = Font(14.0)
        borderpaneConstraints { alignment = Pos.CENTER }
    }

    private val taLog = textarea {
        font = Font(14.0)

        this.maxHeight = Double.MAX_VALUE
        vboxConstraints {
            vgrow = Priority.ALWAYS
            fitToParentWidth()
        }
    }

    private val tblClients = tableview<Client> {

        minWidth = 200.0
        maxWidth = Double.MAX_VALUE
        readonlyColumn("UUID", Client::uuid)
        readonlyColumn("Token", Client::token)

        vboxConstraints {
            vgrow = Priority.ALWAYS
            fitToParentWidth()
        }
    }

    override val root = hbox {
        spacing = 10.0
        padding = Insets(10.0, 10.0, 10.0, 10.0)

        vbox{
            spacing = 5.0

            hbox {
                spacing = 5.0
                alignment = Pos.CENTER

                vbox {
                    spacing = 5.0
                    alignment = Pos.CENTER

                    add(btStart)
                    add(btStop)
                }

                vbox {
                    spacing = 5.0
                    alignment = Pos.CENTER

                    add(btPhoneInfo)
                    add(btContacts)
                    add(btLocation)
                }
            }

            add(taLog)

            vboxConstraints {
                fitToParentWidth()
            }
        }

        add(tblClients)

        tblClients.fitToParentWidth()
    }

    init {
        btStart.action {
            presenter.startServer()
        }

        btStop.action {
            presenter.stopServer()
        }

        btPhoneInfo.action {
            val selectedClient = tblClients.selectedItem ?: return@action
            GlobalState.serverThread?.requestPhoneInfo(selectedClient)
        }

        btContacts.action {
            val selectedClient = tblClients.selectedItem ?: return@action
            GlobalState.serverThread?.requestContacts(selectedClient)
        }

        btLocation.action {
            val selectedClient = tblClients.selectedItem ?: return@action
            GlobalState.serverThread?.requestLocation(selectedClient)
        }
    }

    override fun onUndock() {
        super.onUndock()
        presenter.onUndock()
    }

    fun putToLog(message: String) {
        taLog.appendText("$message\n")
    }

    fun updateTable(clients: List<Client>) {
        clients.forEach {
            for (i in 0 until tblClients.items.size) {
                if (it.uuid == tblClients.items[i].uuid) {
                    tblClients.items[i] = it
                    return@forEach
                }
            }
            tblClients.items.add(it)
        }
    }
}