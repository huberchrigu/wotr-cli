package ch.chrigu.wotr.bot

import ch.chrigu.wotr.gamestate.GameStateFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan

@SpringBootTest(classes = [BotActionFactory::class])
@ComponentScan(basePackageClasses = [HarmFellowshipStrategy::class])
class BotActionFactoryTest(@Autowired private val testee: BotActionFactory) {
    @Test
    fun `should beat free people`() {
        var gameState = GameStateFactory.newGame()
        while (gameState.vpShadow() < 10) {
            gameState = testee.getNext(gameState).apply(gameState)
        }
    }
}
