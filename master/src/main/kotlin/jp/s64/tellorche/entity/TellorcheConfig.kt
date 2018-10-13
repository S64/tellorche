package jp.s64.tellorche.entity

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import jp.s64.tellorche.controller.DebugTelloController
import jp.s64.tellorche.controller.ITelloController

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
        @Json(name = "z_in_cm") val zInCm: Int
)

typealias ControllerId = String

@JsonClass(generateAdapter = true)
data class TelloController(
        @Json(name = "type") val type: ControllerType
)

enum class ControllerType(
        val value: String
) {
    M5STACK("M5Stack"),
    DEBUG("Debug"),
    //
    ;

    fun createInterface(id: ControllerId): ITelloController {
        return when (this) {
            DEBUG -> DebugTelloController(id)
            else -> TODO("ほかのIFつくる")
        }
    }

    object Adapter {

        @FromJson
        fun fromJson(value: String): ControllerType? {
            return ControllerType.values()
                    .find { it.value == value }
        }

    }

}

typealias TimeInMillis =  Long

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
    SET_SPEED(Type.SET, "set_speed"),
    READ_SPEED(Type.READ, "read_speed"),
    READ_BATTERY(Type.READ, "read_battery"),
    READ_TIME(Type.READ, "read_time"),
    //
    ;

    object Adapter {

        @FromJson
        fun fromJson(value: String): TelloCommand? {
            return TelloCommand.values()
                    .find { it.configValue == value }
        }

    }

    enum class Type {
        DO,
        SET,
        READ,
        //
        ;

    }

}
