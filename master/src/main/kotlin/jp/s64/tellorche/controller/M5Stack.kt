package jp.s64.tellorche.controller

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.fazecast.jSerialComm.SerialPort
import jp.s64.tellorche.entity.ControllerId
import jp.s64.tellorche.entity.TelloActionParam
import jp.s64.tellorche.entity.TelloCommand
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.lang.IllegalStateException
import java.nio.Buffer
import java.util.Locale

typealias WifiSsid = String
typealias WifiPassphrase = String
typealias ComPortDescriptor = String

@JsonClass(generateAdapter = true)
data class M5StackControllerConfig(
    @Json(name = "ssid") val ssid: WifiSsid,
    @Json(name = "passphrase") val passphrase: WifiPassphrase,
    @Json(name = "com_descriptor") val comPortDescriptor: ComPortDescriptor
) {

    fun createInterface(id: ControllerId): ITelloController {
        val port = SerialPort.getCommPort(comPortDescriptor)
                .apply { baudRate = 921600 }
                .apply { this.openPort() }

        if (!port.isOpen) {
            TODO("COMが開かない")
        }

        port.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                0, 0
        )

        return M5StackTelloController(id, port, ssid, passphrase)
    }
}

class M5StackTelloController(
    private val id: ControllerId,
    private val port: SerialPort,
    ssid: WifiSsid,
    passphrase: WifiPassphrase
) : ITelloController {

    companion object {

        private const val LINE_SEPARATOR = "\n"
    }

    private val out: PrintStream

    private val background: MessagePrinter

    init {
        background = MessagePrinter(
                `in` = BufferedReader(InputStreamReader(port.inputStream))
        )
        out = PrintStream(port.outputStream, true)

        out.print("wifi_ssid $ssid$LINE_SEPARATOR")
        out.print("wifi_passphrase $passphrase$LINE_SEPARATOR")
        out.print("connect$LINE_SEPARATOR")

        do {
            val line = background.nextCmd()
            if (line == "Wi-Fi connected.") {
                break
            }
        } while (true)
    }

    override fun send(command: TelloCommand, params: List<TelloActionParam>) {
        out.print(command.toCommand(params) + LINE_SEPARATOR)
    }

    override fun dispose() {
        try {
            out.println("disconnect")
            do {
                val line = background.nextCmd()
                if (line == "Wi-Fi disconnected.") {
                    break
                }
            } while (true)
        } finally {
            background.dispose()
            out.close()
            port.closePort()
        }
    }
}

class MessagePrinter(
        private val id: ControllerId,
        private val `in`: BufferedReader
) {

    private val cmds: MutableList<String> = mutableListOf()
    private val itr: Iterator<String> = cmds.iterator()

    private var thread: Thread? = null

    init {
        thread = Thread {
            while (thread != null) {
                val line = `in`.readLine()

                if (line.indexOf("msg: ") == 0) {
                    System.out.println(String.format(
                            Locale.ROOT,
                            "[%s] %s",
                            id,
                            line
                    ))
                } else if (line.indexOf("cmd: ") == 0) {
                    cmds.add(line.substring(5))
                }
            }
        }
    }

    fun nextCmd(): String {
        if (thread == null) {
            throw IllegalStateException("Already disposed.")
        }

        while (itr.hasNext()) {
            // wait
        }

        return itr.next()
    }

    fun dispose() {
        thread = null
    }

}
