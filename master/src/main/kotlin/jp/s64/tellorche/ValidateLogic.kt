package jp.s64.tellorche

import com.fazecast.jSerialComm.SerialPort
import jp.s64.tellorche.entity.ControllerType
import jp.s64.tellorche.entity.ISerialControllerConfig
import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import java.io.PrintStream

class ValidateLogic(
        private val mode: ValidateMode,
        private val out: PrintStream,
        private val err: PrintStream
) {

    companion object {
        private const val EXIT_CODE_OK = 0
        private const val EXIT_CODE_ERR = 1
    }

    private lateinit var schema: Schema
    private lateinit var config: JSONObject

    init {
        javaClass.classLoader.getResourceAsStream("schema.json").use {
            schema = SchemaLoader.load(JSONObject(JSONTokener(it)))
        }
        mode.configFile.inputStream().use {
            config = JSONObject(JSONTokener(it))
        }
    }

    fun exec(): Int {
        try {
            schema.validate(config)
        } catch(e: ValidationException) {
            err.println(e.allMessages)
            return EXIT_CODE_ERR
        }

        val parsed = Tellorche.parseConfig(mode.configFile)

        parsed.controllers.forEach { id, controller ->
            val config: ISerialControllerConfig? = when (controller.type) {
                ControllerType.MICRO_PYTHON -> controller.microPythonConfigs
                ControllerType.ESP32 -> controller.esp32Configs
                else -> null
            }

            config?.let {
                val com = SerialPort.getCommPort(it.comPortDescriptor)
                try {
                    if (!com.openPort()) {
                        warn("$id に設定された ${it.comPortDescriptor} は現在開けません")
                    }
                } finally {
                    com.closePort()
                }
            }
        }

        var invalidControllerId = false
        parsed.sequence.forEach { time, commands ->
            commands.forEachIndexed { i, action ->
                if (!parsed.controllers.keys.containsAll(action.controllers)) {
                    fail("sequence[\"$time\"][$i] で 設定されたcontrollerIdは定義されていません")
                    invalidControllerId = true
                }
            }
        }
        if (invalidControllerId) return EXIT_CODE_ERR

        out.println("ok: この設定ファイルは利用できます")

        return EXIT_CODE_OK
    }

    private fun warn(msg: String) {
        out.println("w: $msg")
    }

    private fun fail(msg: String) {
        err.println("e: $msg")
    }

}
