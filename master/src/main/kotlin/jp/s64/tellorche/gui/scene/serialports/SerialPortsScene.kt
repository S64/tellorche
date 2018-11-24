package jp.s64.tellorche.gui.scene.serialports

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.stage.Modality
import javafx.stage.Stage
import jp.s64.tellorche.SerialPortsLogic
import jp.s64.tellorche.Tellorche
import jp.s64.tellorche.gui.SceneLoader
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream

object SerialPortsSceneFactory {

    private fun create(): Stage = Stage().apply {
        title = "SerialPorts"
        scene = SceneLoader.load("serialports_scene.fxml")
    }

    fun createAndShow(self: Stage) {
        create().apply {
            initOwner(self)
            initModality(Modality.APPLICATION_MODAL)
        }.showAndWait()
    }
}

class SerialPortsController {

    @FXML
    lateinit var output: Label

    @FXML
    lateinit var root: AnchorPane

    private val logic: SerialPortsLogic
    private val buffer: BufferedReader
    private val stream: PrintStream

    private lateinit var thread: Thread

    init {
        val input = PipedInputStream()
        logic = SerialPortsLogic()

        stream = PrintStream(PipedOutputStream(input))
        buffer = BufferedReader(InputStreamReader(input))
    }

    @FXML
    fun initialize() {
        logic.exec(stream, afterClose = true)

        thread = Thread({
            Platform.runLater {
                printlnInGui("java -jar " + Tellorche.filename() + " serialports")
            }
            buffer.forEachLine {
                Platform.runLater {
                    printlnInGui(it)
                }
            }
            Platform.runLater {
                printlnInGui("exited.")
            }
        }, "SerialPorts GUI Printer").apply {
            start()
        }

        root.sceneProperty().addListener { _, _, newScene ->
            newScene.windowProperty().addListener { _, _, newWindow ->
                (newWindow as Stage)
                        .showingProperty()
                        .addListener { observable, oldValue, newValue ->
                            if (oldValue == true && newValue == false) {
                                dispose()
                            }
                        }
            }
        }
    }

    private fun printlnInGui(line: String) {
        output.text += line + System.lineSeparator()
    }

    private fun dispose() {
        stream.close()
    }
}
