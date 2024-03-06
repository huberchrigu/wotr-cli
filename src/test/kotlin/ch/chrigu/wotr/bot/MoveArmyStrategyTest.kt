package ch.chrigu.wotr.bot

import ch.chrigu.wotr.action.MoveAction
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class MoveArmyStrategyTest {
    @Test
    fun `should create 2 times 3 move actions`() {
        val testee = MoveArmyStrategy(mock())
        val army = Figures(listOf(Figure(FigureType.REGULAR, NationName.ISENGARD), Figure(FigureType.ELITE, NationName.ISENGARD), Figure(FigureType.SARUMAN, NationName.ISENGARD)))
        val orthanc = mock<Location> {
            on { nonBesiegedFigures } doReturn army
            on { adjacentLocations } doReturn listOf(LocationName.SOUTH_DUNLAND)
            on { name } doReturn LocationName.ORTHANC
        }
        val state = mock<GameState> {
            on { location } doReturn mapOf(LocationName.ORTHANC to orthanc)
        }
        val result = testee.getActions(state)
        assertThat(result.filterIsInstance<MoveAction>()).hasSize(2 * 3)
    }
}
