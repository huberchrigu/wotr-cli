package ch.chrigu.wotr.bot.dsl

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.At
import ch.chrigu.wotr.gamestate.Reinforcements
import ch.chrigu.wotr.location.LocationName

class ReinforceDsl(private val given: GivenDsl, private val units: String) {
    infix fun to(name: LocationName) {
        val figures = Figures.parse(arrayOf(units), given.gameState.reinforcements)
        given.gameState = given.gameState.move(figures, Reinforcements, At(name))
    }
}
