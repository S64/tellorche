package jp.s64.tellorche.gui.scene.sequence

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyEvent
import javafx.stage.Modality
import javafx.stage.Stage
import jp.s64.tellorche.gui.SceneLoader
import java.io.File

object SequenceSceneFactory {

    private fun create(): Stage = Stage().apply {
        title = "Sequence"
        scene = SceneLoader.load("sequence_scene.fxml")
    }

    fun createAndShow(self: Stage) {
        create().apply {
            initOwner(self)
            initModality(Modality.APPLICATION_MODAL)
        }.showAndWait()
    }
}

class SequenceSceneController {

    @FXML
    lateinit var configFilePathState: Label

    @FXML
    lateinit var configFilePath: TextInputControl

    @FXML
    fun initialize() {
        checkConfigFilePath()
    }

    @FXML
    fun onConfigFilePathChanged(event: KeyEvent) {
        checkConfigFilePath()
    }

    private fun checkConfigFilePath() {
        configFilePathState.text = if (File(configFilePath.text).exists()) {
            "OK"
        } else {
            "NG"
        }
    }
}
