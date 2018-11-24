package jp.s64.tellorche.gui.scene.sequence

import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.stage.Modality
import javafx.stage.Stage
import jp.s64.tellorche.SequenceLogic
import jp.s64.tellorche.SequenceMode
import jp.s64.tellorche.Tellorche
import jp.s64.tellorche.gui.SceneLoader
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream
import java.io.PrintWriter
import kotlin.properties.Delegates

object SequenceExecutionScene {

    private fun create(filename: String, startPeriod: Long): Stage {

        val loader = FXMLLoader(SceneLoader.classLoader().getResource("sequence_execution_scene.fxml"))

        val ret = Stage().apply {
            title = "Sequence Execution"
            scene = Scene(loader.load())
        }

        loader.getController<SequenceExecutionController>()
                .setArgs(filename, startPeriod)

        return ret
    }

    fun createAndShow(self: Stage, filename: String, startPeriod: Long) {
        create(filename, startPeriod).apply {
            initOwner(self)
            initModality(Modality.APPLICATION_MODAL)
        }.showAndWait()
    }
}

class SequenceExecutionController {

    @FXML
    lateinit var output: Label

    @FXML
    lateinit var root: BorderPane

    @FXML
    lateinit var inputField: TextField

    @FXML
    lateinit var sendButton: Button

    @FXML
    lateinit var killButton: Button

    private val input: PipedOutputStream
    private val writer: PrintWriter

    private val buffer: BufferedReader
    private val stream: PrintStream

    private lateinit var filename: String
    private var startPeriod: Long by Delegates.notNull()

    private lateinit var consoleThread: Thread
    private lateinit var logicThread: Thread

    private lateinit var logic: SequenceLogic

    init {
        val input = PipedInputStream()

        stream = PrintStream(PipedOutputStream(input))
        buffer = BufferedReader(InputStreamReader(input))

        this.input = PipedOutputStream()
        this.writer = PrintWriter(this.input)
    }

    @FXML
    fun initialize() {
        root.sceneProperty().addListener { _, _, newScene ->
            newScene.windowProperty().addListener { _, _, newWindow ->
                (newWindow as Stage)
                        .showingProperty()
                        .addListener { _, oldValue, newValue ->
                            if (oldValue == false && newValue == true) {
                                onWindowCreated()
                            } else if (oldValue == true && newValue == false) {
                                dispose()
                            }
                        }
            }
        }
    }

    private fun onWindowCreated() {
        consoleThread = Thread {
            try {
                Platform.runLater {
                    printlnInGui("java -jar " + Tellorche.filename() + " serialports")
                }
                buffer.forEachLine {
                    Platform.runLater {
                        printlnInGui(it)
                    }
                }
            } finally {
                Platform.runLater {
                    printlnInGui("exited.")
                    onKilled()
                }
            }
        }.apply {
            start()
        }

        logicThread = Thread {
            try {
                logic = SequenceLogic(
                        SequenceMode().apply {
                            configFile = File(filename)
                            startAtInMillis = startPeriod
                        },
                        input = BufferedReader(InputStreamReader(PipedInputStream(input))),
                        output = stream,
                        error = stream,
                        isConsole = true
                )
                logic.exec()
            } catch (e: Throwable) {
                e.printStackTrace(stream)
            }
        }.apply {
            start()
        }

        checkInputValue()
    }

    fun setArgs(filename: String, startPeriod: Long) {
        this.filename = filename
        this.startPeriod = startPeriod
    }

    private fun printlnInGui(line: String) {
        output.text += line + System.lineSeparator()
    }

    private fun dispose() {
        stream.close()
    }

    @FXML
    fun onEnterInput(actionEvent: ActionEvent) {
        sendCommand()
    }

    @FXML
    fun onClickKillButton(actionEvent: ActionEvent) {
        logicThread.interrupt()
    }

    fun onInputted(keyEvent: KeyEvent) {
        checkInputValue()
    }

    private fun checkInputValue() {
        sendButton.isDisable = inputField.text.isEmpty()
    }

    fun onClickSendButton(actionEvent: ActionEvent) {
        sendCommand()
    }

    private fun sendCommand() {
        val cmd = inputField.text
        inputField.text = ""
        stream.println(cmd)
        writer.println(cmd)
        writer.flush()
        checkInputValue()
    }

    private fun onKilled() {
        sendButton.isDisable = true
        inputField.isDisable = true
        killButton.isDisable = true
    }
}
