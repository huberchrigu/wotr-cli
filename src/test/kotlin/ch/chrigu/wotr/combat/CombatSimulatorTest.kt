package ch.chrigu.wotr.combat

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.location.LocationType
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombatSimulatorTest {
    private val attacker = Figures.create(2, 2, 0, NationName.SOUTHRONS_AND_EASTERLINGS)
    private val defender = Figures.create(1, 1, 1, NationName.GONDOR)
    private val reinforcements = Figure.create(1, FigureType.REGULAR, NationName.SOUTHRONS_AND_EASTERLINGS) + Figure.create(1, FigureType.REGULAR, NationName.GONDOR)
    private val killed = Figure.create(1, FigureType.REGULAR, NationName.GONDOR)

    @Test
    fun `should reduce elites first`() {
        val newGameState = simulateSouthronsAttackPelargir(
            0, // first round
            6, 4, 6, 6, // three hits
            4, 4, 5 // one hit
        )
        newGameState.location[LocationName.PELARGIR]!!.allFigures.assert().isEmpty()
        newGameState.location[LocationName.WEST_HARONDOR]!!.allFigures.assert().hasArmy(3, 1, 0)
        newGameState.reinforcements.assert().hasArmy(1, 0, 0, NationName.GONDOR)
            .hasArmy(0, 1, 0, NationName.SOUTHRONS_AND_EASTERLINGS)
        newGameState.killed.assert().hasArmy(2, 1, 1)
    }

    @Test
    fun `should downgrade with killed regular`() {
        val newGameState = simulateSouthronsAttackPelargir(
            1, // second round
            2, 4, 5, 1, // one hit
            6, 6 // two hits
        )
        newGameState.location[LocationName.PELARGIR]!!.allFigures.assert().hasArmy(2, 0, 1)
        newGameState.location[LocationName.WEST_HARONDOR]!!.allFigures.assert().hasArmy(2, 1, 0)
        newGameState.reinforcements.assert().hasArmy(1, 1, 0, NationName.SOUTHRONS_AND_EASTERLINGS)
            .hasArmy(1, 0, 0, NationName.GONDOR)
        newGameState.killed.assert().hasArmy(0, 1, 0)
    }

    private fun simulateSouthronsAttackPelargir(round: Int, vararg rolledDice: Int): GameState {
        val gameState = GameState.create(
            listOf(
                Location(LocationName.WEST_HARONDOR, attacker),
                Location(LocationName.PELARGIR, defender)
            ),
            reinforcements,
            killed
        )
        val dice = TestDieFactory(*rolledDice)
        val testee = CombatSimulator(attacker, defender, CombatType.FIELD_BATTLE, LocationType.CITY, LocationName.WEST_HARONDOR, LocationName.PELARGIR, round, dice)
        val result = testee.repeat(1)

        dice.assertAllowDiceRolled()
        val newGameState = result.fold(gameState) { state, casualties -> casualties.apply(state) }
        return newGameState
    }

    class TestDieFactory(vararg list: Int) : DieFactory {
        private val list = list.toList()
        private var i: Int = 0

        override fun next(): Int {
            return list[i].also { i++ }
        }

        fun assertAllowDiceRolled() {
            assertThat(i).isEqualTo(list.size)
        }
    }
}
