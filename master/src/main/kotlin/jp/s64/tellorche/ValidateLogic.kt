package jp.s64.tellorche

import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import java.io.PrintStream

class ValidateLogic(
        mode: ValidateMode,
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
        return EXIT_CODE_OK
    }

}
