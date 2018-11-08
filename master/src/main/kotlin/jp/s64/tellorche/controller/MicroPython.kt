package jp.s64.tellorche.controller

import com.fazecast.jSerialComm.SerialPort
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import jp.s64.tellorche.entity.ControllerId
import jp.s64.tellorche.entity.TelloActionParam
import jp.s64.tellorche.entity.TelloCommand
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.lang.IllegalStateException
import java.util.Collections
import java.util.Locale

@JsonClass(generateAdapter = true)
data class MicroPythonControllerConfig(
        @Json(name = "ssid") val ssid: WifiSsid,
        @Json(name = "passphrase") val passphrase: WifiPassphrase,
        @Json(name = "com_descriptor") val comPortDescriptor: ComPortDescriptor
) {

    fun createInterface(id: ControllerId): ITelloController {
        val port = SerialPort.getCommPort(comPortDescriptor)
                .apply { baudRate = 921600 }
                .apply { this.openPort() }

        if (!port.isOpen) {
            TODO("COM ($comPortDescriptor) が開かない")
        }

        port.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                0, 0
        )

        return MicroPythonTelloController(id, port, ssid, passphrase)
    }
}

class MicroPythonTelloController(
        id: ControllerId,
        private val port: SerialPort,
        ssid: WifiSsid,
        passphrase: WifiPassphrase
) : ITelloController {

    override fun doCrash() {
        try {
            out.println("!crash")
        } finally {
            doFinally()
        }
    }

    private val out: PrintStream
    private val background: MicroPythonMessagePrinter

    init {
        background = MicroPythonMessagePrinter(
                id = id,
                `in` = BufferedReader(InputStreamReader(port.inputStream))
        )
        out = PrintStream(port.outputStream, true)

        sendReset()

        do {
            val line = background.nextCmd()
            if (line == "wakeup.") {
                break;
            }
        } while (true)

        sendControllerCommand("wifi_ssid $ssid")
        sendControllerCommand("wifi_passphrase $passphrase")
        sendControllerCommand("connect")

        do {
            val line = background.nextCmd()
            if (line == "Wi-Fi connected.") {
                break
            }
        } while (true)
    }

    override fun send(command: TelloCommand, params: List<TelloActionParam>) {
        out.println("cmd-tello: ${command.toCommand(params)}")
    }

    fun sendReset() {
        out.println("!reset")
        do {
            val line = background.nextCmd()
            if (line == "Wi-Fi disconnected.") {
                break
            }
        } while (true)
    }

    fun sendControllerCommand(cmd: String) {
        out.println("cmd-controller: $cmd")
    }

    override fun dispose() {
        try {
            sendReset()
        } finally {
            doFinally()
        }
    }

    private fun doFinally() {
        background.dispose()
        out.close()
        port.closePort()
    }

}

class MicroPythonMessagePrinter (
        private val id: ControllerId,
        private val `in`: BufferedReader
) {

    private val cmds: MutableList<String> = Collections.synchronizedList(mutableListOf())
    private var lastRead: Int = -1

    private var thread: Thread? = null

    init {
        thread = Thread {
            while (thread != null) {
                val line = `in`.readLine()

                if (line == null) {
                    break
                } else if (line.indexOf("cmd: ") == 0) {
                    cmds.add(line.substring(5))
                }

                System.out.println(String.format(
                        Locale.ROOT,
                        "[%s] %s",
                        id,
                        line
                ))
            }
        }
        thread!!.start()
    }

    fun nextCmd(): String {
        if (thread == null) {
            throw IllegalStateException("Already disposed.")
        }

        while (cmds.size == (lastRead + 1) && thread != null) {
            Thread.sleep(10)
        }
        lastRead++

        return cmds[lastRead]
    }

    fun dispose() {
        thread = null
    }

}
