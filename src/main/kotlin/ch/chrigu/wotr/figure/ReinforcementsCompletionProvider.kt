package ch.chrigu.wotr.figure

import ch.chrigu.wotr.gamestate.GameStateHolder
import org.springframework.shell.CompletionContext
import org.springframework.stereotype.Component

@Component
class ReinforcementsCompletionProvider(gameStateHolder: GameStateHolder) : AbstractFiguresCompletionProvider(gameStateHolder) {
    override fun getAllFigures(context: CompletionContext) = gameStateHolder.current.reinforcements

    override fun getWhoValue(context: CompletionContext): List<String> = if (context.words.first().startsWith("-"))
        context.words.drop(1)
    else
        context.words
}
