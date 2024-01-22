package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.DieAction
import ch.chrigu.wotr.action.EventType
import ch.chrigu.wotr.action.PlayEventAction
import ch.chrigu.wotr.dice.DieUsage
import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.gamestate.GameState
import org.jline.terminal.Terminal
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(2)
@Component
class HarmFellowshipStrategy(private val terminal: Terminal) : BotStrategy {
    override fun getActions(state: GameState) = listOfNotNull(
        getEventAction(state),
        getNazgulAction(state),
        getArmyAction(state)
    )

    private fun getNazgulAction(state: GameState): ProposedBotAction? {
        val fellowshipLocation = state.fellowshipLocation
        val dice = state.dice.shadow.getDiceToMoveCharacters()
        return if (fellowshipLocation.allFigures().containsNazgul() || !fellowshipLocation.canMoveTo(NAZGUL) || dice.isEmpty())
            null
        else
            DieAction(getBest(dice), getMoveCharactersAction(state))
    }

    private fun getArmyAction(state: GameState): ProposedBotAction? {
        val fellowshipLocation = state.fellowshipLocation
        val dice = state.dice.shadow.getDiceToMoveArmy()
        return if (fellowshipLocation.allFigures().containsShadowArmyUnit() || !fellowshipLocation.canMoveTo(SHADOW_UNIT) || dice.isEmpty())
            null // TODO: A character die without leadership has no worth
        else
            DieAction(getBest(dice), getMoveArmyAction(state))
    }

    private fun getEventAction(state: GameState): ProposedBotAction {
        val (dieToPlayCharacterEvent, points) = selectBest(state.dice.shadow.getDiceToPlayCharacterEvent()) ?: return null
        val action = DieAction(dieToPlayCharacterEvent, PlayEventAction(EventType.CHARACTER, terminal))
        return if (state.dice.freePeople.getDiceToPlayCharacterEvent().isNotEmpty() && state.fellowship.numStepsLeft(state) == 1)
            ProposedBotAction(Int.MAX_VALUE, action)
        else
            ProposedBotAction(points, action) // TODO: merge with other actions?
    }

    /**
     * * If there are dice that works without ring use these:
     *   * If there are more character dice left, use one of these.
     *   * Otherwise, use event die.
     * * Among the die that can be used with rings, use the first one in this priority order: muster, army, army&muster
     */
    private fun selectBest(diceToPlayCharacterEvent: List<DieUsage>): Pair<DieUsage, Int>? { // TODO: Generalize and reuse?
        if (diceToPlayCharacterEvent.isEmpty()) return null
        val withoutRing = diceToPlayCharacterEvent.filter { !it.useRing }
        return if (withoutRing.isEmpty()) {
            listOf(DieType.MUSTER, DieType.ARMY, DieType.ARMY_MUSTER)
                .firstNotNullOf { type -> diceToPlayCharacterEvent.firstOrNull { it.use == type } }
                .let { it to 1 }
        } else {
            val eventDice = withoutRing.filter { it.use == DieType.EVENT }
            val characterDice = withoutRing.filter { it.use == DieType.CHARACTER }
            if (characterDice.size > eventDice.size) characterDice.first() to 5 else eventDice.first() to 5
        }
    }
}
