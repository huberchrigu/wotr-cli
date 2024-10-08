package ch.chrigu.wotr.combat

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.toFigures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.location.LocationType
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CombatSimulatorTest {
    @Test
    fun `should reduce elites first`() {
        val attacker = Figures.create(2, 2, 0, NationName.SOUTHRONS_AND_EASTERLINGS)
        val defender = Figures.create(1, 1, 1, NationName.GONDOR)
        val gameState = GameState.create(
            listOf(
                Location(LocationName.WEST_HARONDOR, attacker),
                Location(LocationName.PELARGIR, defender)
            ),
            Figure.create(1, FigureType.REGULAR, NationName.SOUTHRONS_AND_EASTERLINGS)
        )
        val dice = TestDieFactory(
            6, 4, 6, 6, // three hits
            4, 4, 5 // one hit
        )
        val testee = CombatSimulator(attacker, defender, CombatType.FIELD_BATTLE, LocationType.CITY, LocationName.WEST_HARONDOR, LocationName.PELARGIR, 0, dice)
        val result = testee.repeat(1)

        dice.assertAllowDiceRolled()
        val newGameState = result.fold(gameState) { state, casualties -> casualties.apply(state) }
        assertThat(newGameState.location[LocationName.PELARGIR]!!.allFigures()).isEmpty()
        newGameState.location[LocationName.WEST_HARONDOR]!!.allFigures().toFigures().assert().hasArmy(3, 1, 0)
        newGameState.reinforcements.assert().hasArmy(0, 1, 0)
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
