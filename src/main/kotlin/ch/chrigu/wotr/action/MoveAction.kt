package ch.chrigu.wotr.action

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName

class MoveAction(private val fromLocation: LocationName, private val toLocation: LocationName, private val figures: Figures) : GameAction {
    override fun apply(oldState: GameState) = oldState.removeFrom(fromLocation, figures).addTo(toLocation, figures)

    override fun toString() = "Move $figures from $fromLocation to $toLocation"
}
