package ch.chrigu.wotr.bot

import ch.chrigu.wotr.gamestate.GameState
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.math.max

@Order(0)
@Component
class ThrowDiceStrategy : BotStrategy {
    override fun getAction(state: GameState): ProposedBotAction? {
        return if (state.dice.shadow.isEmpty())
            ProposedBotAction(Int.MAX_VALUE, AssignEyesAndThrowDiceAction(numEyes(state)))
        else
            null
    }

    private fun numEyes(state: GameState) = if (state.fellowship.mordor == null)
        max(1, state.fellowship.numRerolls(state))
    else if (state.fellowship.remainingCorruption() < 5)
        state.fellowship.remainingCorruption()
    else 2
}
