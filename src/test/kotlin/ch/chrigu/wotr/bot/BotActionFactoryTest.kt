package ch.chrigu.wotr.bot

import ch.chrigu.wotr.gamestate.GameStateFactory
import org.jline.terminal.Terminal
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@SpringBootTest(classes = [BotActionFactory::class])
@Import(BotActionFactoryTest.Config::class)
class BotActionFactoryTest(@Autowired private val testee: BotActionFactory) {
    @MockBean
    private lateinit var terminal: Terminal

    @Test
    fun `should beat free people`() {
        var gameState = GameStateFactory.newGame()
        while (gameState.vpShadow() < 10) {
            gameState = testee.getNext(gameState).apply(gameState)
        }
    }

    @TestConfiguration
    @ComponentScan(basePackageClasses = [HarmFellowshipStrategy::class])
    class Config
}
