package ch.chrigu.wotr.action

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.toFigures
import ch.chrigu.wotr.gamestate.At
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.gamestate.getPoolZoneFor
import ch.chrigu.wotr.location.LocationName

class DowngradeAction(private val location: LocationName, private val elite: Figure, private val regular: Figure) : GameAction {
    override fun apply(oldState: GameState) = KillAction(location, elite.toFigures()).apply(oldState)
        .move(regular.toFigures(), oldState.getPoolZoneFor(regular), At(location))
}
