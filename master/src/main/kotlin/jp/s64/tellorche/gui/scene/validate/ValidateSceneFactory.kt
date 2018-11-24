package jp.s64.tellorche.gui.scene.validate

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.stage.Modality
import javafx.stage.Stage
import jp.s64.tellorche.Tellorche
import jp.s64.tellorche.ValidateLogic
import jp.s64.tellorche.gui.SceneLoader
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream

object ValidateSceneFactory {

    private fun create(filename: String): Stage {

        val loader = FXMLLoader(SceneLoader.classLoader().getResource("validate_scene.fxml"))

        val ret = Stage().apply {
            title = "Validate"
            scene = Scene(loader.load())
        }

        loader.getController<ValidateController>()
                .setArgs(filename)

        return ret
    }

    fun createAndShow(self: Stage, filename: String) {
        create(filename).apply {
            initOwner(self)
            initModality(Modality.APPLICATION_MODAL)
        }.showAndWait()
    }
}

class ValidateController {

    @FXML
    lateinit var root: AnchorPane

    @FXML
    lateinit var output: Label

    private lateinit var filename: String
    private lateinit var buffer: BufferedReader
    private lateinit var logic: ValidateLogic

    @FXML
    fun initialize() {
        root.sceneProperty().addListener { _, _, newScene ->
            newScene.windowProperty().addListener { _, _, newWindow ->
                (newWindow as Stage)
                        .showingProperty()
                        .addListener { _, oldValue, newValue ->
                            if (oldValue == false && newValue == true) {
                                onWindowCreated()
                            }
                        }
            }
        }
    }

    fun setArgs(filename: String) {
        this.filename = filename

        val input = PipedInputStream()
        buffer = BufferedReader(InputStreamReader(input))

        val writer = PrintStream(PipedOutputStream(input))
        logic = ValidateLogic(File(filename), writer, writer)
    }

    private fun onWindowCreated() {
        Thread({
            printlnInGui("java -jar " + Tellorche.filename() + " validate --config " + filename)
            try {
                buffer.forEachLine {
                    printlnInGui(it)
                }
            } finally {
                printlnInGui("exited")
            }
        }, "ValidateScene GUI Printer").apply {
            start()
        }

        Thread({
            logic.exec()
        }, "ValidateScene Logic Bridge").apply {
            start()
        }
    }

    private fun printlnInGui(msg: String) {
        Platform.runLater {
            output.text += msg + System.lineSeparator()
        }
    }
}
