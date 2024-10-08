package ch.chrigu.wotr.action

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.toFigures
import ch.chrigu.wotr.gamestate.At
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.gamestate.Killed
import ch.chrigu.wotr.gamestate.Reinforcements
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.player.Player

class KillAction(private val locationName: LocationName, private val figures: Figures) : GameAction {
    override fun apply(oldState: GameState) = oldState.move(getShadowUnits(), At(locationName), Reinforcements)
        .move(getTransientUnits(), At(locationName), Killed)

    private fun getShadowUnits() = figures.all.filter { it.nation.player == Player.SHADOW && !it.type.isUniqueCharacter }.toFigures()
    private fun getTransientUnits() = figures.all.filter { it.nation.player != Player.SHADOW || it.type.isUniqueCharacter }.toFigures()

    override fun toString() = "Kill $figures at $locationName"
}
