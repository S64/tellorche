package jp.s64.tellorche

import jp.s64.tellorche.entity.TimeInMillis
import jp.s64.tellorche.gui.TellorcheGui
import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.spi.SubCommand
import org.kohsuke.args4j.spi.SubCommandHandler
import org.kohsuke.args4j.spi.SubCommands
import java.io.File
import java.net.URI
import java.nio.file.Paths

object Tellorche {

    @JvmStatic
    fun main(orgArgs: Array<String>) {
        val args = Args()
        val parser = CmdLineParser(args)

        try {
            parser.parseArgument(*orgArgs)
        } catch (e: CmdLineException) {
            parser.printUsage(System.err)
            throw e
        }

        when (args.mode) {
            is SequenceMode -> SequenceLogic(args.mode as SequenceMode).exec()
            is SerialPortsMode -> SerialPortsLogic().exec(System.out, afterClose = false)
            is GuiMode -> TellorcheGui().exec()
        }
    }

    fun filename(): String {
        return Paths.get(javaClass.protectionDomain.codeSource.location.toURI())
                .fileName.toString()
    }

}

class Args {

    @Argument(handler = SubCommandHandler::class, required = true, index = 0)
    @SubCommands(
        SubCommand(name = "sequence", impl = SequenceMode::class),
        SubCommand(name = "serialports", impl = SerialPortsMode::class),
        SubCommand(name = "gui", impl = GuiMode::class)
    )
    lateinit var mode: Mode
}

class SequenceMode : Mode() {

    @Option(name = "--config", metaVar = "path", required = true)
    lateinit var configFile: File

    @Option(name = "--startAt", metaVar = "timeInMillis", required = false)
    var startAtInMillis: TimeInMillis? = null
}

class SerialPortsMode : Mode() {}

class GuiMode : Mode() {}

sealed class Mode
