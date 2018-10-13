package jp.s64.tellorche.controller

import jp.s64.tellorche.entity.ControllerId
import jp.s64.tellorche.entity.TelloActionParam
import jp.s64.tellorche.entity.TelloCommand
import java.util.*

class DebugTelloController(
        private val id: ControllerId
) : ITelloController {

    override fun send(command: TelloCommand, params: List<TelloActionParam>) {
        println(
            String.format(
                    Locale.ROOT,
                    "Debug[%s]: `%s %s`",
                    id, command, params.joinToString(" ")
            )
        )
    }

}
