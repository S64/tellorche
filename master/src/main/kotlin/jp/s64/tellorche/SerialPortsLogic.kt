package jp.s64.tellorche

import com.fazecast.jSerialComm.SerialPort
import java.io.PrintStream
import java.util.Locale

class SerialPortsLogic {

    fun exec(output: PrintStream, afterClose: Boolean) {
        SerialPort.getCommPorts().forEachIndexed { i, port ->
            output.println(String.format(
                    Locale.ROOT,
                    "%d. `%s`: \"%s\"",
                    i, port.systemPortName, port.portDescription
            ))
        }
        if (afterClose) {
            output.close()
        }
    }
}
