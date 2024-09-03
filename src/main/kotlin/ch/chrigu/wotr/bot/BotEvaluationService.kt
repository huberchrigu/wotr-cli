package ch.chrigu.wotr.bot

import ch.chrigu.wotr.card.Cards
import ch.chrigu.wotr.dice.Dice
import ch.chrigu.wotr.dice.DiceAndRings
import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.fellowship.Fellowship
import ch.chrigu.wotr.figure.NumberedLevel
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.nation.Nation
import ch.chrigu.wotr.player.Player
import kotlin.math.max
import kotlin.math.min

/**
 * Computes a score for the situation of the shadow bot. Plus points are considered to be good for the shadow. Minus points for the free people.
 */
object BotEvaluationService {
    /**
     * The game state score is composed of:
     * * The [victory point score][countVp]
     * * The [fellowship score][countFellowship]
     * * The sum of all [location scores][LocationEvaluationService.scoreFor]
     * * The [shadow card score][countCards]
     * * The [politics score of all nations][countNations]
     */
    fun count(state: GameState): Int {
        val locationEvaluationService = LocationEvaluationService(state)
        return countDice(state.dice) +
                countVp(state) +
                countFellowship(state.fellowship, state) +
                state.location.values.sumOf { locationEvaluationService.scoreFor(it) } +
                countCards(state.cards) +
                countNations(state.nation.values)
    }

    /**
     * The nearer a nation is to be at war, the more points they get (less for free people). An active nation also counts more.
     */
    private fun countNations(nations: Collection<Nation>) = nations.sumOf { if (it.name.player == Player.SHADOW) countNation(it) else -countNation(it) }

    private fun countNation(nation: Nation) = if (nation.isAtWar())
        10
    else if (nation.active)
        8 - nation.box
    else
        4 - nation.box

    /**
     * The win states have an extrem high/low score. Otherwise, it's just a mix of both player's vps, where the free people's count more.
     */
    private fun countVp(state: GameState) = if (state.vpShadow() >= 10)
        100
    else if (state.vpFreePeople() >= 4)
        -90
    else
        5 * state.vpShadow() - 10 * state.vpFreePeople()

    /**
     * The rings give the most points. Then, the more types of dice the player has available the better.
     */
    private fun countDice(dice: Dice) = (count(dice.shadow, Player.SHADOW) - count(dice.freePeople, Player.FREE_PEOPLE)) / 2
    private fun count(diceAndRings: DiceAndRings, player: Player) = 10 * diceAndRings.rings + diceAndRings.rolled
        .groupBy { it }.entries.sumOf { (type, list) ->
            when (type) {
                DieType.WILL_OF_THE_WEST -> 3
                DieType.ARMY_MUSTER -> max(3, list.size)
                DieType.ARMY -> if (player == Player.SHADOW) 2 else 1
                DieType.CHARACTER -> if (player == Player.FREE_PEOPLE) 2 else 1
                else -> 1
            }
        }

    /**
     * Win states a very high/low score. Otherwise, corruption, progress, discovery and remaining companions play a role.
     */
    private fun countFellowship(fellowship: Fellowship, state: GameState) = if (fellowship.corruption >= Fellowship.MAX_CORRUPTION)
        100
    else if (fellowship.mordor != null && fellowship.mordor >= Fellowship.MORDOR_STEPS)
        -100
    else
        fellowship.corruption * 2 - fellowship.progress - (fellowship.mordor ?: 0) * 2 +
                (if (fellowship.discovered) 5 else 0) +
                fellowship.numRerolls(state) * 5 -
                state.companions.sumOf { (it.type.level as? NumberedLevel)?.number ?: 0 } * 2

    private fun countCards(cards: Cards) = min(4, cards.numCards())
}
