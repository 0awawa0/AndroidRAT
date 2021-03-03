import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FcmOptions
import javafx.stage.Stage
import tornadofx.App
import tornadofx.launch
import ui.main.MainView
import java.io.FileInputStream
import kotlin.system.exitProcess

class Application: App(MainView::class) {

    companion object {
        var instance: Application? = null
            private set
    }

    init { instance = this }

    override fun start(stage: Stage) {
        super.start(stage)

        stage.minWidth = 800.0
        stage.minHeight = 275.0

        stage.setOnCloseRequest {
            exitProcess(0)
        }
    }
}


fun main(args: Array<String> = emptyArray()) {
    val token = FileInputStream("./fcm_token.json")
    val options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(token)).build()
    FirebaseApp.initializeApp(options)

    launch<Application>(args)
}