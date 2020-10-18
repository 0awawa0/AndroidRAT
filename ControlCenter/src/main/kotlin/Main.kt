import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import ui.main.MainView
import kotlin.system.exitProcess

class Application: App(MainView::class) {

    companion object {
        var instance: Application? = null
            private set
    }

    init { instance = this }

    override fun start(stage: Stage) {
        super.start(stage)

        stage.minWidth = 500.0
        stage.minHeight = 275.0

        stage.setOnCloseRequest {
            exitProcess(0)
        }
    }
}


fun main(args: Array<String> = emptyArray()) {
    launch<Application>(args)
}