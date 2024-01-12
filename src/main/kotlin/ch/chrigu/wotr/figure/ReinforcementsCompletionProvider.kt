package ch.chrigu.wotr.figure

import ch.chrigu.wotr.gamestate.GameStateHolder
import org.springframework.stereotype.Component

@Component
class ReinforcementsCompletionProvider(gameStateHolder: GameStateHolder) : AbstractFiguresCompletionProvider(gameStateHolder)
