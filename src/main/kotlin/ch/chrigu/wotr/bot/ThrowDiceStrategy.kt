package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.AssignEyesAndThrowDiceAction
import ch.chrigu.wotr.gamestate.GameState
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.math.max

@Order(0)
@Component
class ThrowDiceStrategy : BotStrategy {
    override fun getActions(state: GameState): List<ProposedBotAction> {
        return if (state.dice.shadow.isEmpty())
            listOf(ProposedBotAction(Int.MAX_VALUE, AssignEyesAndThrowDiceAction(numEyes(state))))
        else
            emptyList()
    }

    private fun numEyes(state: GameState) = if (state.fellowship.mordor == null)
        max(1, state.fellowship.numRerolls(state))
    else if (state.fellowship.remainingCorruption() < 5)
        state.fellowship.remainingCorruption()
    else 2
}
