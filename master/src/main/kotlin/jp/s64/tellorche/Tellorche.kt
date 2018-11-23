package jp.s64.tellorche

import jp.s64.tellorche.entity.TimeInMillis
import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.spi.SubCommand
import org.kohsuke.args4j.spi.SubCommandHandler
import org.kohsuke.args4j.spi.SubCommands
import java.io.File

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
            is SerialPortsMode -> SerialPortsLogic().exec()
            is ValidateMode -> TODO()
        }
    }
}

class Args {

    @Argument(handler = SubCommandHandler::class, required = true, index = 0)
    @SubCommands(
        SubCommand(name = "sequence", impl = SequenceMode::class),
        SubCommand(name = "serialports", impl = SerialPortsMode::class),
        SubCommand(name = "validate", impl = ValidateMode::class)
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

class ValidateMode : Mode() {

    @Option(name = "--config", metaVar = "path", required = true)
    lateinit var configFile: File

}

sealed class Mode
