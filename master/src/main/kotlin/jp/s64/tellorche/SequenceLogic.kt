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
import java.io.PrintStream
import java.io.PrintWriter

class SequenceLogic(
    private val args: SequenceMode,
    private val output: PrintStream,
    private val error: PrintStream
) {

    private val moshi = Moshi.Builder()
            .add(ControllerType.Adapter)
            .add(TelloCommand.Adapter)
            .build()

    private val config: TellorcheConfig

    private val controllers: Map<ControllerId, ITelloController>

    private var safeEnded: Boolean = false

    init {
        config = TellorcheConfigJsonAdapter(moshi).fromJson(
                args.configFile.readText(Charsets.UTF_8)
        ) ?: TODO("Parse error.")

        controllers = config.controllers.mapValues {
            it.value.createInterface(it.key, output = output, error = error)
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            if (!safeEnded) {
                error.println("Do crash!")
                controllers.forEach {
                    it.value.doCrash()
                }
            } else {
                output.println("Safe shutdown.")
            }
        })
    }

    fun exec() {
        do {
            output.println("[Tellorche] Config file loaded. Input `exec` to start sequence:")
            if (readLine() == "exec") break
        } while (true)

        // exec
        mainLoop(args.startAtInMillis)
        safeEnded = true

        controllers.forEach {
            it.value.dispose()
        }
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
                output.println("[Tellorche] Period: ($currentTick)...$toExecuteTick:")
            }

            targets.forEach { timeInMillis ->
                val seq = config.sequence[timeInMillis]!!

                seq.forEach { action ->
                    action.controllers.forEach { controllerId ->
                        doControl(
                                controllerId,
                                action.command,
                                action.params
                        )
                    }
                }

                executedPeriods.add(timeInMillis)
            }

            if (executedPeriods.size == config.sequence.size) {
                output.println("[Tellorche] All sequences was executed.")
                break
            }
        } while (true)
    }

    private fun doControl(id: ControllerId, command: TelloCommand, orgParams: List<TelloActionParam>) {
        val params = TelloCommand.convertParams(command, orgParams, config.scale)

        controllers[id]!!
                .send(command, params)
    }
}
