package jp.s64.tellorche.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import jp.s64.tellorche.controller.ITelloController
import com.fazecast.jSerialComm.SerialPort
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream

typealias WifiSsid = String
typealias WifiPassphrase = String
typealias ComPortDescriptor = String

@JsonClass(generateAdapter = true)
data class Esp32ControllerConfig(
        @Json(name = "ssid") val ssid: WifiSsid,
        @Json(name = "passphrase") val passphrase: WifiPassphrase,
        @Json(name = "com_descriptor") val comPortDescriptor: ComPortDescriptor
) {

    fun createInterface(id: ControllerId): ITelloController {
        val port = SerialPort.getCommPort(comPortDescriptor)
                .apply { this.openPort() }

        if (!port.isOpen) {
            TODO("COMが開かない")
        }

        port.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                0, 0
        )

        return Esp32TelloController(id, port, ssid, passphrase)
    }

}

class Esp32TelloController(
        private val id: ControllerId,
        private val port: SerialPort,
        ssid: WifiSsid,
        passphrase: WifiPassphrase
) : ITelloController {

    private val `in`: BufferedReader
    private val out: PrintStream

    init {
        `in` = BufferedReader(InputStreamReader(port.inputStream))
        out = PrintStream(port.outputStream)

        out.println("wifi_ssid $ssid")
        out.println("wifi_passphrase $passphrase")

        do {
            val line = `in`.readLine()
            if (line == "Wi-Fi connected.") {
                break
            }
        } while (true)
    }

    override fun send(command: TelloCommand, params: List<TelloActionParam>) {
        out.println(command.toCommand(params))
    }

    override fun dispose() {
        try {
            out.println("disconnect")
            do {
                val line = `in`.readLine()
                if (line == "Wi-Fi disconnected.") {
                    break
                }
            } while (true)
        } finally {
            `in`.close()
            out.close()
            port.closePort()
        }
    }

}