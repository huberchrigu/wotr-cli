package ch.chrigu.wotr.bot.dsl

import ch.chrigu.wotr.nation.NationName

class NationDsl(private val given: GivenDsl, private val name: NationName) {
    fun atWar() {
        given.gameState = given.gameState.updateNation(name) { copy(active = true, box = 0) }
    }
}
