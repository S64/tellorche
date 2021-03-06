package jp.s64.tellorche.entity

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import jp.s64.tellorche.controller.ComPortDescriptor
import jp.s64.tellorche.controller.DebugTelloController
import jp.s64.tellorche.controller.ITelloController
import jp.s64.tellorche.controller.ESP32ControllerConfig
import jp.s64.tellorche.controller.MicroPythonControllerConfig
import java.io.PrintStream

@JsonClass(generateAdapter = true)
data class TellorcheConfig(
    @Json(name = "accuracy_in_millis") val accuracy: TimeInMillis,
    @Json(name = "scale") val scale: TellorcheScale,
    @Json(name = "controllers") val controllers: Map<ControllerId, TelloController>,
    @Json(name = "sequence") val sequence: Map<TimeInMillis, List<TelloAction>>
)

@JsonClass(generateAdapter = true)
data class TellorcheScale(
    @Json(name = "x_in_cm") val xInCm: Int,
    @Json(name = "y_in_cm") val yInCm: Int,
    @Json(name = "z_in_cm") val zInCm: Int,
    @Json(name = "speed_in_cm_per_sec") val speedInCmPerSec: Int
)

typealias ControllerId = String

@JsonClass(generateAdapter = true)
data class TelloController(
    @Json(name = "type") val type: ControllerType,
    @Json(name = "type-esp32-config") val esp32Configs: ESP32ControllerConfig?,
    @Json(name = "type-micropython-config") val microPythonConfigs: MicroPythonControllerConfig?
) {

    fun createInterface(id: ControllerId, output: PrintStream, error: PrintStream): ITelloController {
        return when (type) {
            ControllerType.DEBUG -> DebugTelloController(id, output = output, error = error)
            ControllerType.ESP32 -> esp32Configs!!.createInterface(id, output = output, error = error)
            ControllerType.MICRO_PYTHON -> microPythonConfigs!!.createInterface(id, output = output, error = error)
        }
    }
}

enum class ControllerType(
    val value: String
) {
    ESP32("ESP32"),
    DEBUG("Debug"),
    MICRO_PYTHON("MicroPython")
    //
    ;

    object Adapter {

        @FromJson
        fun fromJson(value: String): ControllerType? {
            return ControllerType.values()
                    .find { it.value == value }
        }
    }
}

interface ISerialControllerConfig {

    val comPortDescriptor: ComPortDescriptor
}

typealias TimeInMillis = Long

@JsonClass(generateAdapter = true)
data class TelloAction(
    val command: TelloCommand,
    val params: List<TelloActionParam>,
    val controllers: List<ControllerId>
)

typealias TelloActionParam = String

enum class TelloCommand(
    val type: Type,
    val configValue: String
) {
    KEEPALIVE(Type.DO, "tellorche-keepalive"),
    COMMAND(Type.MODE, "command"),
    TAKEOFF(Type.DO, "takeoff"),
    LAND(Type.DO, "land"),
    UP(Type.DO, "up"),
    DOWN(Type.DO, "down"),
    LEFT(Type.DO, "left"),
    RIGHT(Type.DO, "right"),
    FORWARD(Type.DO, "forward"),
    BACK(Type.DO, "back"),
    CW(Type.DO, "cw"),
    CCW(Type.DO, "ccw"),
    FLIP(Type.DO, "flip"),
    GO(Type.DO, "go"),
    CURVE(Type.DO, "curve"),
    SET_SPEED(Type.SET, "set_speed"),
    READ_SPEED(Type.READ, "read_speed"),
    READ_BATTERY(Type.READ, "read_battery"),
    READ_TIME(Type.READ, "read_time"),
    //
    ;

    fun toCommand(params: List<TelloActionParam>): String {
        val command: String = when (this) {
            SET_SPEED -> "speed"
            READ_SPEED -> "Speed?"
            READ_BATTERY -> "Battery?"
            READ_TIME -> "Time?"
            else -> this.configValue
        }

        return (listOf(command) + params).joinToString(" ")
    }

    object Adapter {

        @FromJson
        fun fromJson(value: String): TelloCommand? {
            return TelloCommand.values()
                    .find { it.configValue == value }
        }
    }

    enum class Type {
        MODE,
        DO,
        SET,
        READ,
        //
        ;
    }

    companion object {

        fun convertParams(command: TelloCommand, params: List<TelloActionParam>, scale: TellorcheScale): List<TelloActionParam> {
            return when (command) {
                UP, DOWN -> {
                    return listOf((params[0].toInt() * scale.yInCm).toString())
                }
                LEFT, RIGHT -> {
                    return listOf((params[0].toInt() * scale.xInCm).toString())
                }
                FORWARD, BACK -> {
                    return listOf((params[0].toInt() * scale.zInCm).toString())
                }
                SET_SPEED -> {
                    return listOf((params[0].toInt() * scale.speedInCmPerSec).toString())
                }
                GO -> {
                    return listOf(
                            params[0].toInt() * scale.xInCm,
                            params[1].toInt() * scale.yInCm,
                            params[2].toInt() * scale.zInCm,
                            params[3].toInt() * scale.speedInCmPerSec
                    ).map { it.toString() }
                }
                CURVE -> {
                    return listOf(
                            params[0].toInt() * scale.xInCm,
                            params[1].toInt() * scale.yInCm,
                            params[2].toInt() * scale.zInCm,
                            params[3].toInt() * scale.xInCm,
                            params[4].toInt() * scale.yInCm,
                            params[5].toInt() * scale.zInCm,
                            params[6].toInt() * scale.speedInCmPerSec
                    ).map { it.toString() }
                }
                else -> params
            }
        }
    }
}
