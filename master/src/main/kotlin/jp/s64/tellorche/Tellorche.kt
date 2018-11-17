package jp.s64.tellorche

import com.squareup.moshi.Moshi
import jp.s64.tellorche.controller.ITelloController
import jp.s64.tellorche.entity.ControllerId
import jp.s64.tellorche.entity.ControllerType
import jp.s64.tellorche.entity.TelloActionParam
import jp.s64.tellorche.entity.TelloCommand
import jp.s64.tellorche.entity.TellorcheConfig
import jp.s64.tellorche.entity.TellorcheConfigJsonAdapter
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
        }
    }

}

class Args {

    @Argument(handler = SubCommandHandler::class, required = true, index = 0)
    @SubCommands(
        SubCommand(name = "sequence", impl = SequenceMode::class)
    )
    lateinit var mode: Mode

}

class SequenceMode : Mode() {

    @Option(name = "--config", metaVar = "path", required = true)
    lateinit var configFile: File

    @Option(name = "--startAt", metaVar = "timeInMillis", required = false)
    var startAtInMillis: TimeInMillis? = null

}

sealed class Mode
