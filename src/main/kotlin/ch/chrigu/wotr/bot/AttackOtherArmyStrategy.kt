package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.gamestate.GameState
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(4)
@Component
class AttackOtherArmyStrategy : BotStrategy {
    override fun getActions(state: GameState): List<GameAction> {
        TODO("Not yet implemented")
    }
}
