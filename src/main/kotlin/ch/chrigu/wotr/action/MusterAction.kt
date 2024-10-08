package ch.chrigu.wotr.action

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.At
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.gamestate.Reinforcements
import ch.chrigu.wotr.location.LocationName

class MusterAction(private val figures: Figures, private val locationName: LocationName) : GameAction {
    override fun apply(oldState: GameState) = oldState.move(figures, Reinforcements, At(locationName))

    override fun toString() = "Muster $figures at $locationName"
}
