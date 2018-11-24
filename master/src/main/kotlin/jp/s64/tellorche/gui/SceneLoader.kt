package jp.s64.tellorche.gui

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene

object SceneLoader {

    fun classLoader() = javaClass.classLoader

    fun load(filename: String) = Scene(
            FXMLLoader.load<Parent>(
                    classLoader().getResource(filename)
            )
    )
}
