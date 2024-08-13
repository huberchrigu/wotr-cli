package ch.chrigu.wotr.bot

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class LocationEvaluationServiceTest {
    private val umbarArmy = Figures.create(3, 0, 0, NationName.SOUTHRONS_AND_EASTERLINGS)
    private val pelargirArmy = Figures.create(1, 0, 0, NationName.GONDOR)
    private val nearHaradArmy = Figures.create(3, 1, 0, NationName.SOUTHRONS_AND_EASTERLINGS)
    private val initialLocations = listOf(
        Location(LocationName.UMBAR, umbarArmy),
        Location(LocationName.WEST_HARONDOR, Figures.empty()),
        Location(LocationName.PELARGIR, pelargirArmy),
        Location(LocationName.NEAR_HARAD, nearHaradArmy)
    )
    private val state = GameState(initialLocations.associateBy { it.name }, emptyMap(), Figures(emptyList(), FiguresType.REINFORCEMENTS), emptyList())

    @Test
    fun `merging armies should get higher score`() {
        val allMovedToUmbar = state.removeFrom(LocationName.NEAR_HARAD, nearHaradArmy).addTo(LocationName.UMBAR, nearHaradArmy)
        val one = nearHaradArmy.subSet(1, 0, 0, null)
        val onlyOneMovedToUmbar = state.removeFrom(LocationName.NEAR_HARAD, one).addTo(LocationName.UMBAR, one)
        assertThat(allMovedToUmbar) isBetterThan onlyOneMovedToUmbar
    }

    @Test
    fun `should get lower score if there is an army in the way`() {
        val withFreePeopleArmyInWestHarandor = state.addTo(LocationName.WEST_HARONDOR, Figures.create(1, 0, 0, NationName.GONDOR))
        assertThat(state) isBetterThan withFreePeopleArmyInWestHarandor
    }

    @Test
    fun `moving towards the target should get higher score`() {
        val armyAtWestHarandor = state.removeFrom(LocationName.UMBAR, umbarArmy).removeFrom(LocationName.NEAR_HARAD, nearHaradArmy)
            .addTo(LocationName.WEST_HARONDOR, umbarArmy + nearHaradArmy)
        assertThat(armyAtWestHarandor) isBetterThan state
    }

    private fun assertThat(state: GameState) = GameStateAssertion(state, initialLocations.map { it.name })

    class GameStateAssertion(private val state: GameState, private val locations: List<LocationName>) {
        infix fun isBetterThan(other: GameState) {
            assertThat(countScore(state)).isGreaterThan(countScore(other))
        }

        private fun countScore(forState: GameState): Int {
            val testee = LocationEvaluationService(forState)
            return locations.sumOf { testee.scoreFor(forState.location[it]!!) }
        }
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setLogLevel() {
            val logger = LoggerFactory.getLogger(LocationEvaluationService::class.java) as Logger
            logger.level = Level.DEBUG
        }
    }
}
