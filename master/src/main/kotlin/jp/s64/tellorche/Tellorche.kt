package jp.s64.tellorche

import com.squareup.moshi.Moshi
import jp.s64.tellorche.entity.ControllerType
import jp.s64.tellorche.entity.TelloCommand
import jp.s64.tellorche.entity.TellorcheConfig
import jp.s64.tellorche.entity.TellorcheConfigJsonAdapter
import jp.s64.tellorche.entity.TimeInMillis
import jp.s64.tellorche.gui.TellorcheGui
import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.spi.SubCommand
import org.kohsuke.args4j.spi.SubCommandHandler
import org.kohsuke.args4j.spi.SubCommands
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
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
            is SequenceMode -> SequenceLogic(args.mode as SequenceMode, input = BufferedReader(InputStreamReader(System.`in`)), output = System.out, error = System.err, isConsole = false).exec()
            is SerialPortsMode -> SerialPortsLogic().exec(System.out, afterClose = false)
            is GuiMode -> TellorcheGui().exec()
            is ValidateMode -> System.exit(ValidateLogic(args.mode as ValidateMode, out = System.out, err = System.err).exec())
        }
    }

    fun filename(): String {
        return Paths.get(javaClass.protectionDomain.codeSource.location.toURI())
                .fileName.toString()
    }

    private val moshi = Moshi.Builder()
            .add(ControllerType.Adapter)
            .add(TelloCommand.Adapter)
            .build()

    fun parseConfig(configFile: File): TellorcheConfig {
        return TellorcheConfigJsonAdapter(moshi).fromJson(
                configFile.readText(Charsets.UTF_8)
        ) ?: TODO("Parse error.")
    }

}

class Args {

    @Argument(handler = SubCommandHandler::class, required = true, index = 0)
    @SubCommands(
        SubCommand(name = "sequence", impl = SequenceMode::class),
        SubCommand(name = "serialports", impl = SerialPortsMode::class),
        SubCommand(name = "gui", impl = GuiMode::class),
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

class SerialPortsMode : Mode()

class GuiMode : Mode()

class ValidateMode : Mode() {

    @Option(name = "--config", metaVar = "path", required = true)
    lateinit var configFile: File

}

sealed class Mode
