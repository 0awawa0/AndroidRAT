package ui.main

import tornadofx.View
import tornadofx.vbox

class MainView: View("ControlCenter") {

    private val presenter = MainPresenter(this)

    override val root = vbox {

    }
}