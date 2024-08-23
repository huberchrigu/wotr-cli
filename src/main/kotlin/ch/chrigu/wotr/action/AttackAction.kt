package ch.chrigu.wotr.action

import ch.chrigu.wotr.card.EventType
import ch.chrigu.wotr.combat.CombatSimulator
import ch.chrigu.wotr.combat.CombatType
import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import org.jline.terminal.Terminal

/**
 * @param defenderLocation If equal to [attackerLocation], it is a sortie or a siege battle.
 */
data class AttackAction(
    private val terminal: Terminal,
    private val attacker: Figures,
    private val defender: Figures,
    private val attackerLocation: LocationName,
    private val defenderLocation: LocationName = attackerLocation
) : GameAction {
    override fun apply(oldState: GameState): GameState {
        terminal.writer().println(toString())
        terminal.writer().println("Search an appropriate combat card from ${getNumCombatCards()} ${getDeckType()}")
        return oldState
    }

    override fun simulate(oldState: GameState): GameState {
        val casualties = CombatSimulator(
            attacker, defender, getCombatType(oldState), oldState.location[defenderLocation]!!.type,
            attackerLocation, defenderLocation
        )
            .repeat(20)
        return casualties.fold(oldState) { a, b -> b.apply(a) }
    }

    override fun toString() = "$attacker ($attackerLocation) attacks $defender (${defenderLocation})"
    override fun requiredDice() = setOf(DieType.ARMY, DieType.ARMY_MUSTER) + if (attacker.all.any { !it.type.isUnit })
        setOf(DieType.CHARACTER)
    else
        emptySet()

    private fun getNumCombatCards() = if (figures().any { it.type == FigureType.WITCH_KING })
        3
    else
        2

    private fun figures() = attacker.all + defender.all

    private fun getCombatType(state: GameState) = if (defenderLocation == attackerLocation) {
        if (state.location[attackerLocation]!!.nonBesiegedFigures.containsAll(attacker))
            CombatType.SIEGE
        else
            CombatType.SORTIE
    } else
        CombatType.FIELD_BATTLE

    private fun getDeckType() = if (figures().count { it.isNazgulOrWitchKing() } > 3)
        EventType.CHARACTER
    else
        EventType.STRATEGY

    companion object {
        fun create(terminal: Terminal, gameState: GameState, from: LocationName, to: LocationName, figures: Figures) = AttackAction(
            terminal,
            figures,
            if (from == to) gameState.location[to]!!.besiegedFigures else gameState.location[to]!!.nonBesiegedFigures,
            from,
            to
        )
    }
}
