package ch.chrigu.wotr.figure

import ch.chrigu.wotr.gamestate.GameStateHolder
import ch.chrigu.wotr.location.LocationName
import org.springframework.shell.CompletionContext
import org.springframework.stereotype.Component

@Component
class FiguresCompletionProvider(gameStateHolder: GameStateHolder) : AbstractFiguresCompletionProvider(gameStateHolder) {
    override fun getAllFigures(context: CompletionContext): Figures {
        val locationName = LocationName.get(getFromValue(context))
        val location = gameStateHolder.current.location[locationName]!!
        return location.nonBesiegedFigures
    }

    override fun getWhoValue(context: CompletionContext) = context.words.dropWhile { it != "-w" && it != "--who" }.drop(1)

    private fun getFromValue(context: CompletionContext) = if (context.words.first().startsWith("-"))
        context.words[1]
    else
        context.words.first()
}
