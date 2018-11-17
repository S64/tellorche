package jp.s64.tellorche

import com.fazecast.jSerialComm.SerialPort
import java.util.Locale

class PortsLogic {

    fun exec() {
        SerialPort.getCommPorts().forEachIndexed { i, port ->
            System.out.println(String.format(
                    Locale.ROOT,
                    "%d. `%s`: \"%s\"",
                    i, port.systemPortName, port.portDescription
            ))
        }
    }

}