package jp.s64.tellorche.gui.scene.sequence

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import jp.s64.tellorche.gui.SceneLoader
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

    private lateinit var filename: String
    private var startPeriod: Long by Delegates.notNull()

    fun setArgs(filename: String, startPeriod: Long) {
        this.filename = filename
        this.startPeriod = startPeriod
    }

}
