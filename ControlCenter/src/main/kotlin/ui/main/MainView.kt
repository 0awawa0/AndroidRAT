package ui.main

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Priority
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

    private val taLog = textarea {
        font = Font(14.0)

        this.maxHeight = Double.MAX_VALUE
        this.maxWidth = Double.MAX_VALUE
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

            vbox {
                spacing = 5.0
                alignment = Pos.CENTER

                add(btStart)
                add(btStop)
            }
            add(taLog)

            vboxConstraints {
                fitToParentWidth()
            }
        }


    }

    init {
        btStart.action {
            Server.instance.start()
        }

        btStop.action {
            Server.instance.stopThread()
        }
    }

    override fun onUndock() {
        super.onUndock()
        presenter.onUndock()
    }

    fun putToLog(message: String) {
        taLog.appendText("$message\n")
    }
}