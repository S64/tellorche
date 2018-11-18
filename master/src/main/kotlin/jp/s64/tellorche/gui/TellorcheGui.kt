package jp.s64.tellorche.gui

import javafx.stage.Stage
import jp.s64.tellorche.gui.scene.first.FirstSceneFactory

class TellorcheGui {

    fun exec() {
        javafx.application.Application
                .launch(App::class.java)
    }
}

class App : javafx.application.Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            title = "Tellorche GUI"
            scene = FirstSceneFactory.create()
        }.show()
    }
}
