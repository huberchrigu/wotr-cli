package ch.chrigu.wotr.bot.dsl

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName

class UnitActionDsl(private val gameStateProvider: GameStateProvider, private val units: String, private val action: UnitActionInvocation) {
    infix fun from(from: LocationName) = UnitActionFromDsl(gameStateProvider, units, from, action)
}

class UnitActionFromDsl(
    private val gameStateProvider: GameStateProvider,
    private val units: String,
    private val from: LocationName,
    private val action: UnitActionInvocation
) {
    infix fun to(to: LocationName) {
        val figures = Figures.parse(arrayOf(units), from, gameStateProvider.gameState)
        gameStateProvider.gameState = action(gameStateProvider.gameState, from, to, figures)
    }
}

interface GameStateProvider {
    var gameState: GameState
}
typealias UnitActionInvocation = GameState.(from: LocationName, to: LocationName, figures: Figures) -> GameState
