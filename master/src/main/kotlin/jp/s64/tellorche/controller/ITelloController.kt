package jp.s64.tellorche.controller

import jp.s64.tellorche.entity.TelloActionParam
import jp.s64.tellorche.entity.TelloCommand

interface ITelloController {

    fun send(command: TelloCommand, params: List<TelloActionParam>)

    fun dispose()

    fun doCrash()
}
