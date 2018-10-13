package jp.s64.tellorche

import com.squareup.moshi.Moshi
import jp.s64.tellorche.entity.*
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.io.File

object Tellorche {

    private val moshi = Moshi.Builder()
            .add(ControllerType.Adapter)
            .add(TelloCommand.Adapter)
            .build()

    private lateinit var config: TellorcheConfig

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

        args.let {
            it.configFile.readText(Charsets.UTF_8)
                    .let { config = TellorcheConfigJsonAdapter(moshi).fromJson(it) ?: TODO("Parse Error") }
        }

        do {
            println("[Tellorche] Config file loaded. Input `exec` to start sequence:")
            if (readLine() == "exec") break
        } while (true)

        // exec
        mainLoop(args.startAtInMillis)
    }

    private fun mainLoop(skip: TimeInMillis?) {
        val initAt = System.currentTimeMillis()
        val executedPeriods = mutableSetOf<TimeInMillis>()

        val startTick: TimeInMillis

        if (skip != null) {
            executedPeriods.addAll(config.sequence.keys.filter { it < skip })
            startTick = skip
        } else {
            startTick = 0
        }

        do {
            val tickStartAt = System.currentTimeMillis()
            val currentTick = (tickStartAt - initAt) + startTick
            val toExecuteTick = currentTick + config.accuracy

            val targets = config.sequence.keys.filter { it <= toExecuteTick && !executedPeriods.contains(it) }

            if (targets.isNotEmpty()) {
                println("[Tellorche] Period: ($currentTick)...$toExecuteTick:")
            }

            targets.forEach { timeInMillis ->
                val seq = config.sequence[timeInMillis]!!

                seq.forEach { action ->
                    action.controllers.forEach { controllerId ->
                        val controller = config.controllers[controllerId]!!
                        doControl(
                                controllerId,
                                controller,
                                action.command,
                                action.params
                        )
                    }
                }

                executedPeriods.add(timeInMillis)
            }

            if (executedPeriods.size == config.sequence.size) {
                println("[Tellorche] All sequences was executed.")
                break
            }
        } while (true)
    }

    private fun doControl(id: ControllerId, controller: TelloController, command: TelloCommand, orgParams: List<TelloActionParam>) {
        val params = TelloCommand.convertParams(command, orgParams, config.scale)

        controller.type
                .createInterface(id)
                .send(command, params)
    }

}

class Args {

    @Option(name = "--config", metaVar = "path", required = true)
    lateinit var configFile: File

    @Option(name = "--startAt", metaVar = "timeInMillis", required = false)
    var startAtInMillis: TimeInMillis? = null

}
