package ch.chrigu.wotr.action

import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName

class MusterFiguresAction(private val musters: Map<Figures, LocationName>) : GameAction {
    private val actions = musters.map { (figures, location) -> MusterAction(figures, location) }

    constructor(figures: Figures, locationName: LocationName) : this(mapOf(figures to locationName))

    constructor(vararg muster: Pair<Figure, LocationName>) : this(muster.associate { (f, l) -> Figures(listOf(f)) to l })

    override fun apply(oldState: GameState): GameState = actions.fold(oldState) { state, action ->
        action.apply(state)
    }

    override fun requiredDice() = setOf(DieType.MUSTER, DieType.ARMY_MUSTER)

    operator fun plus(other: MusterFiguresAction?) = other?.let { MusterFiguresAction(musters + it.musters) }

    override fun toString() = actions.joinToString(", ")
}
