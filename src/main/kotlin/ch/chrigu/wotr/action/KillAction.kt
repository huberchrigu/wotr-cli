package ch.chrigu.wotr.action

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.player.Player

class KillAction(private val locationName: LocationName, private val figures: Figures) : GameAction {
    override fun apply(oldState: GameState) = oldState.removeFrom(locationName, figures)
        .addToReinforcements(figures.getArmy().filter { it.nation.player == Player.SHADOW && !it.type.isUniqueCharacter })
}
