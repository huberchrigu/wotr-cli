package ch.chrigu.wotr.bot.dsl

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName

class UnitActionDsl(private val given: GivenDsl, private val units: String, private val action: UnitActionInvocation) {
    infix fun from(from: LocationName) = UnitActionFromDsl(given, units, from, action)
}

class UnitActionFromDsl(
    private val given: GivenDsl,
    private val units: String,
    private val from: LocationName,
    private val action: UnitActionInvocation
) {
    infix fun to(to: LocationName) {
        val figures = Figures.parse(arrayOf(units), from, given.gameState)
        given.gameState = action(given.gameState, from, to, figures)
    }
}

typealias UnitActionInvocation = GameState.(from: LocationName, to: LocationName, figures: Figures) -> GameState
