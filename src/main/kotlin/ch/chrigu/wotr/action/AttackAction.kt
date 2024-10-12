package ch.chrigu.wotr.action

import ch.chrigu.wotr.card.EventType
import ch.chrigu.wotr.combat.CombatSimulator
import ch.chrigu.wotr.combat.CombatType
import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.toFigures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.LocationName
import org.jline.terminal.Terminal

/**
 * @param defenderLocation If equal to [attackerLocation], it is a sortie or a siege battle.
 */
data class AttackAction(
    private val terminal: Terminal,
    val attacker: Figures,
    val defender: Figures,
    val attackerLocation: LocationName,
    val defenderLocation: LocationName = attackerLocation
) : GameAction {
    init {
        require(attacker.armyPlayer != null && defender.armyPlayer != null) { "Attacker or defender contains no army" }
    }

    override val alteringLocations = listOf(attackerLocation, defenderLocation)

    override fun apply(oldState: GameState): GameState {
        checkPreconditions(oldState)
        terminal.writer().println(toString())
        terminal.writer().println("Search an appropriate combat card from ${getNumCombatCards()} ${getDeckType()}")
        return oldState.nationsGetAttacked(defender.getArmyNations())
    }

    /**
     * Simplification. Defender never moves away.
     */
    override fun simulate(oldState: GameState): GameState {
        checkPreconditions(oldState)
        val newState = simulateCombat(oldState.nationsGetAttacked(defender.getArmyNations()))
        return moveIfPossible(newState)
    }

    private fun simulateCombat(oldState: GameState, round: Int = 0, attackers: Figures = attacker, defenders: Figures = defender): GameState {
        val casualties = CombatSimulator(
            attackers, defenders, getCombatType(oldState), oldState.location[defenderLocation]!!.type,
            attackerLocation, defenderLocation, round
        ).repeat(20)
        val newState = casualties.fold(oldState) { a, b -> b.apply(a) }
        return simulateOtherRound(newState, round) ?: newState
    }

    private fun simulateOtherRound(newState: GameState, round: Int): GameState? {
        val type = getCombatType(newState)
        val remainingAttackers = getRemainingAttackers(newState).downgradeOrNull(type)
        val remainingDefenders = getRemainingDefenders(newState)
        return if (remainingAttackers == null || remainingAttackers.isEmpty() || remainingDefenders.isEmpty() || !remainingAttackers.isSuperior(remainingDefenders, type))
            null
        else
            simulateCombat(newState, round + 1, remainingAttackers, remainingDefenders)
    }

    private fun moveIfPossible(newState: GameState) = MoveAction(
        attackerLocation, defenderLocation,
        getRemainingAttackers(newState)
    ).tryToApply(newState) ?: newState

    private fun getRemainingDefenders(newState: GameState) = getRemainingFigures(newState, defenderLocation, defender)
    private fun getRemainingAttackers(newState: GameState) = getRemainingFigures(newState, attackerLocation, attacker)
    private fun getRemainingFigures(newState: GameState, at: LocationName, initialFigures: Figures) =
        newState.location[at]!!.allFigures().intersect(initialFigures.all.toSet()).toFigures()

    override fun toString() = "$attacker ($attackerLocation) attacks $defender (${defenderLocation})"
    override fun requiredDice() = setOf(DieType.ARMY, DieType.ARMY_MUSTER) + if (attacker.all.any { !it.type.isUnit })
        setOf(DieType.CHARACTER)
    else
        emptySet()

    private fun getNumCombatCards() = if (figures().any { it.type == FigureType.WITCH_KING })
        3
    else
        2

    private fun Figures.isSuperior(defenders: Figures, type: CombatType) = if (type == CombatType.SIEGE)
        score(false) > defenders.score(false) * 2
    else
        score(false) > defenders.score(false)

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

    private fun checkPreconditions(state: GameState) {
        val nationsNotAtWar = attacker.getArmy().map { it.nation }.distinct()
            .mapNotNull { state.nation[it] }
            .filter { !it.isAtWar() }
        check(nationsNotAtWar.isEmpty()) { "Attacker should be at war, but nations are not: $nationsNotAtWar" }
    }

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
