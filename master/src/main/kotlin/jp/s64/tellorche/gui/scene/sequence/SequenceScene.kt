package jp.s64.tellorche.gui.scene.sequence

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import jp.s64.tellorche.gui.SceneLoader
import jp.s64.tellorche.gui.scene.validate.ValidateSceneFactory
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
    lateinit var execButton: Button

    @FXML
    lateinit var validateButton: Button

    @FXML
    lateinit var root: VBox

    @FXML
    fun initialize() {
        checkConfigFilePath()
    }

    @FXML
    fun onConfigFilePathChanged(event: KeyEvent) {
        checkConfigFilePath()
    }

    private fun checkConfigFilePath() {
        val file = File(configFilePath.text)
        val flag = file.exists() && file.isFile

        execButton.isDisable = !flag
        validateButton.isDisable = !flag

        configFilePathState.text = if (flag) {
            "OK"
        } else {
            "NG"
        }
    }

    @FXML
    fun onExecuteClicked(actionEvent: ActionEvent) {
        SequenceExecutionScene
                .createAndShow(
                        root.scene.window as Stage,
                        filename = configFilePath.text,
                        startPeriod = 0L
                )
    }

    fun onClickConfigPickButton(actionEvent: ActionEvent) {
        val result = FileChooser()
                .showOpenDialog(root.scene.window as Stage)

        result?.let {
            configFilePath.text = it.absolutePath
            checkConfigFilePath()
        }
    }

    fun onValidateClicked(actionEvent: ActionEvent) {
        ValidateSceneFactory
                .createAndShow(
                        root.scene.window as Stage,
                        configFilePath.text
                )
    }
}
