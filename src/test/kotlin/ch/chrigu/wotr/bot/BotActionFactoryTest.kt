package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.AssignEyesAndThrowDiceAction
import ch.chrigu.wotr.action.AttackAction
import ch.chrigu.wotr.action.DieAction
import ch.chrigu.wotr.action.GameAction
import ch.chrigu.wotr.bot.dsl.GameStateProvider
import ch.chrigu.wotr.bot.dsl.GivenDsl
import ch.chrigu.wotr.bot.dsl.UnitActionDsl
import ch.chrigu.wotr.bot.dsl.given
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.gamestate.GameStateFactory
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.Assertions.assertThat
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

@SpringBootTest(classes = [BotActionFactory::class], properties = ["logging.level.ch.chrigu.wotr.bot.BotActionFactory=DEBUG"])
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
            val location = next.alteredObjects(gameState).joinToString(", ")
            println("$next -> $location\n$gameState")
        }
    }

    @Test
    fun `should attack minas tirith`() {
        given(GameStateFactory.newGame()) {
            nation(NationName.SAURON).atWar()
            nation(NationName.GONDOR).atWar()
            remove("200") from LocationName.OSGILIATH
            reinforce("811 (sa)") to LocationName.OSGILIATH
        } expectNextAction {
            assignEyesAndThrowDice(1)
            attack("811") from LocationName.OSGILIATH to LocationName.MINAS_TIRITH
        }
    }

    infix fun GivenDsl.expectNextAction(apply: ExpectActionDsl.() -> Unit) = ExpectActionDsl(gameState, testee).apply()
    class ExpectActionDsl(override var gameState: GameState, private val botActionFactory: BotActionFactory) : GameStateProvider {
        fun attack(units: String) = UnitActionDsl(this, units) { from, to, figures ->
            assertAction<AttackAction> { action ->
                assertThat(action.attackerLocation).isEqualTo(from)
                assertThat(action.defenderLocation).isEqualTo(to)
                assertThat(action.attacker).isEqualTo(figures)
            }
        }

        fun assignEyesAndThrowDice(numEyes: Int) {
            gameState = assertAction<AssignEyesAndThrowDiceAction> {
                assertThat(it.numEyes).isEqualTo(numEyes)
            }
        }

        private inline fun <reified T : GameAction> assertAction(assert: (T) -> Unit): GameState {
            val action = botActionFactory.getNext(gameState)
            val actual = if (action is DieAction) action.actions.first() else action
            assertThat(actual.javaClass).isEqualTo(T::class.java)
            check(actual is T)
            assert(actual)
            return actual.simulate(gameState)
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
