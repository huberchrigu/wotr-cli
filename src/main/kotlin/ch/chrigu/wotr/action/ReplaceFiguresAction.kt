package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.*
import ch.chrigu.wotr.location.LocationName

class ReplaceFiguresAction(private val replace: Figures, private val by: Figures, private val location: LocationName) : GameAction {
    override fun apply(oldState: GameState) = oldState.move(replace, At(location), Reinforcements)
        .move(by, Reinforcements, At(location))

    override fun requiredDice() = setOf(DieType.MUSTER, DieType.ARMY_MUSTER)

    override fun toString() = "Replace $replace by $by at $location"
}
