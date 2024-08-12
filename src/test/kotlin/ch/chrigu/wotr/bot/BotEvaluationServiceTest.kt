package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.*
import ch.chrigu.wotr.card.EventType
import ch.chrigu.wotr.dice.DieType
import ch.chrigu.wotr.dice.DieUsage
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.gamestate.GameStateFactory
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class BotEvaluationServiceTest {
    private val initial = GameStateFactory.newGame()
        .let { it.copy(dice = it.dice.fakeShadowRoll(DieType.MUSTER, DieType.ARMY, DieType.EVENT)) }
    private val initialScore = BotEvaluationService.count(initial)

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(Args::class)
    fun `should have higher score after action`(description: String, action: (GameState) -> GameAction) {
        val newState = action(initial).apply(initial)
        val newScore = BotEvaluationService.count(newState)
        assertThat(newScore).describedAs(description).isGreaterThan(initialScore)
    }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(MoveActionArgs::class)
    fun `should prefer move action over other action`(
        description: String,
        betterFrom: LocationName,
        betterTo: LocationName,
        betterWho: String,
        worseFrom: LocationName,
        worseTo: LocationName,
        worseWho: String
    ) {
        val betterAction = countMove(betterFrom, betterTo, betterWho)
        val worseAction = countMove(worseFrom, worseTo, worseWho)
        assertThat(betterAction).isGreaterThan(worseAction)
    }

    class Args : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<Arguments> = Stream.of(
            action("MoveAction", DieType.ARMY) { MoveAction(LocationName.GORGOROTH, LocationName.MINAS_MORGUL, Figures.parse(arrayOf("1"), LocationName.GORGOROTH, it)) },
            action("MusterAction", DieType.MUSTER) { MusterAction(Figures.parse(arrayOf("1"), it.reinforcements, NationName.SAURON), LocationName.DOL_GULDUR) },
            action("DrawEventAction", DieType.EVENT) { DrawEventAction(EventType.CHARACTER) },
            action("PoliticsMarkerAction", DieType.MUSTER) { PoliticsMarkerAction(NationName.SAURON) }
        ).flatMap { it.toArguments() }

        private fun action(description: String, dieType: DieType, action: (GameState) -> GameAction) = ActionArgument(description, dieType, action)

        class ActionArgument(private val description: String, private val dieType: DieType, private val action: (GameState) -> GameAction) {
            fun toArguments(): Stream<Arguments> = Stream.of(
                Arguments.of(description, action),
                Arguments.of("DieAction($description)", toDieAction())
            )

            private fun toDieAction(): (GameState) -> GameAction = { DieAction(DieUsage(dieType, false, Player.SHADOW), listOf(action(it))) }
        }
    }

    class MoveActionArgs : ArgumentsProvider {
        override fun provideArguments(p0: ExtensionContext): Stream<Arguments> = Stream.of(
            Arguments.of("should assemble army before moving", LocationName.FAR_HARAD, LocationName.NEAR_HARAD, "310", LocationName.UMBAR, LocationName.WEST_HARONDOR, "300"),
            Arguments.of(
                "should move directly to where an army can be a threat", LocationName.FAR_HARAD, LocationName.NEAR_HARAD, "310", LocationName.FAR_HARAD, LocationName.KHAND, "310"
            )
        )
    }

    private fun countMove(from: LocationName, to: LocationName, who: String) = BotEvaluationService.count(move(from, to, who).apply(initial))
    private fun move(from: LocationName, to: LocationName, who: String) = MoveAction(from, to, Figures.parse(arrayOf(who), from, initial))
}
