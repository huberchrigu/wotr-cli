package ch.chrigu.wotr.bot.dsl

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.location.LocationName

class RemoveDsl(private val given: GivenDsl, private val units: String) {
    infix fun from(name: LocationName) {
        val figures = Figures.parse(arrayOf(units), name, given.gameState)
        given.gameState = given.gameState.removeFrom(name, figures)
    }
}
