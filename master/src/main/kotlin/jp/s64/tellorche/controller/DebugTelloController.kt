package jp.s64.tellorche.controller

import jp.s64.tellorche.entity.ControllerId
import jp.s64.tellorche.entity.TelloActionParam
import jp.s64.tellorche.entity.TelloCommand
import java.io.PrintStream
import java.util.Locale

class DebugTelloController(
    private val id: ControllerId,
    private val output: PrintStream,
    private val error: PrintStream
) : ITelloController {

    override fun doCrash() {
        TODO("doCrashを実装する")
    }

    override fun send(command: TelloCommand, params: List<TelloActionParam>) {
        output.println(
            String.format(
                    Locale.ROOT,
                    "Debug[%s]: `%s`",
                    id, command.toCommand(params)
            )
        )
    }

    override fun dispose() {
        output.println(
                String.format(
                        Locale.ROOT,
                        "Debug[%s]: disposed.",
                        id
                )
        )
    }
}
