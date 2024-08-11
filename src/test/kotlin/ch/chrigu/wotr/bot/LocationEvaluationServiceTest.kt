package ch.chrigu.wotr.bot

import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocationEvaluationServiceTest {
    private val umbarArmy = Figures.create(3, 0, 0, NationName.SOUTHRONS_AND_EASTERLINGS)
    private val pelargirArmy = Figures.create(1, 0, 0, NationName.GONDOR)
    private val nearHaradArmy = Figures.create(3, 1, 1, NationName.SOUTHRONS_AND_EASTERLINGS)
    private val initialLocations = listOf(
        Location(LocationName.UMBAR, umbarArmy),
        Location(LocationName.WEST_HARONDOR, Figures.empty()),
        Location(LocationName.PELARGIR, pelargirArmy),
        Location(LocationName.NEAR_HARAD, nearHaradArmy)
    )
    private val state = GameState(initialLocations.associateBy { it.name }, emptyMap(), Figures(emptyList()), emptyList())

    @Test
    fun `merging armies should get higher score`() {
        val withoutNearHarad = state.removeFrom(LocationName.NEAR_HARAD, nearHaradArmy)
        assertThat(withoutNearHarad.addTo(LocationName.UMBAR, nearHaradArmy)) isBetterThan withoutNearHarad.addTo(LocationName.WEST_HARONDOR, nearHaradArmy)
    }

    @Test
    fun `should get lower score if there is an army in the way`() {
        TODO()
    }

    @Test
    fun `moving towards the target should get higher score`() {
        TODO()
    }

    private fun assertThat(state: GameState) = GameStateAssertion(state)

    class GameStateAssertion(private val state: GameState, private val locations: List<Location>) {
        infix fun isBetterThan(other: GameState) {
            assertThat(countScore(state)).isGreaterThan(countScore(other))
        }

        private fun countScore(forState: GameState): Int {
            val testee = LocationEvaluationService(forState)
            return locations.sumOf { testee.scoreFor(it) }
        }
    }
}
