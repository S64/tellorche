package jp.s64.tellorche.gui.scene.first

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import jp.s64.tellorche.gui.SceneLoader
import jp.s64.tellorche.gui.scene.sequence.SequenceSceneFactory
import jp.s64.tellorche.gui.scene.serialports.SerialPortsSceneFactory

object FirstSceneFactory {

    fun create(): Scene = SceneLoader.load("first_scene.fxml")
}

class FirstSceneController {

    @FXML
    lateinit var root: VBox

    @FXML
    lateinit var sequenceButton: Button

    @FXML
    fun onClickExecuteSequence(e: ActionEvent) {
        SequenceSceneFactory
                .createAndShow(root.scene.window as Stage)
    }

    @FXML
    fun onClickSerialPortsButton(e: ActionEvent) {
        SerialPortsSceneFactory
                .createAndShow(root.scene.window as Stage)
    }
}
