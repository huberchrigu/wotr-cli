package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.DrawEventAction
import ch.chrigu.wotr.card.EventType
import ch.chrigu.wotr.gamestate.GameState
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(10)
class DrawEventStrategy : BotStrategy {
    override fun getActions(state: GameState) = EventType.entries.map { DrawEventAction(it) }
}
