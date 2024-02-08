package ch.chrigu.wotr.bot

import ch.chrigu.wotr.gamestate.GameStateFactory
import org.jline.terminal.Terminal
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import java.io.PrintWriter

@SpringBootTest(classes = [BotActionFactory::class])
@Import(BotActionFactoryTest.Config::class)
class BotActionFactoryTest(@Autowired private val testee: BotActionFactory) {
    @MockBean
    private lateinit var terminal: Terminal

    @Test
    fun `should beat free people`() {
        var gameState = GameStateFactory.newGame()
        while (gameState.vpShadow() < 10) {
            val next = testee.getNext(gameState)
            gameState = next.simulate(gameState)
            println("$next -> $gameState")
        }
    }

    @BeforeEach
    fun initTerminal() {
        val writer = mock<PrintWriter> {
            on { this.println(any<String>()) } doAnswer {
                println(it.arguments[0] as String)
            }
        }
        whenever(terminal.writer()) doReturn writer
    }

    @TestConfiguration
    @ComponentScan(basePackageClasses = [HarmFellowshipStrategy::class])
    class Config
}
