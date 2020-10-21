package ui.main

import javafx.geometry.Pos
import javafx.scene.text.Font
import server.Server
import tornadofx.*


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

    override val root = vbox {

        alignment = Pos.CENTER

        add(btStart)
        add(btStop)
    }

    init {
        btStart.action {
            Server.instance.start()
        }

        btStop.action {
            Server.instance.stopThread()
        }
    }
}