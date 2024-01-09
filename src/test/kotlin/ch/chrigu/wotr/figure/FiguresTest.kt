package ch.chrigu.wotr.figure

import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FiguresTest {
    @Test
    fun `should allow opposed character on field`() {
        val shadowArmy = Figures(
            listOf(
                Figure(FigureType.REGULAR, NationName.SAURON),
                Figure(FigureType.ELITE, NationName.SOUTHRONS_AND_EASTERLINGS),
                Figure(FigureType.LEADER_OR_NAZGUL, NationName.SAURON)
            )
        )
        val withAragorn = Figures(listOf(Figure(FigureType.ARAGORN, NationName.GONDOR))) + shadowArmy
        assertThat(withAragorn.armyPlayer).isEqualTo(Player.SHADOW)
        assertThat(withAragorn.getArmy().size).isEqualTo(3)
        assertThat(withAragorn.getArmyPerNation().keys).isEqualTo(setOf(NationName.SAURON, NationName.SOUTHRONS_AND_EASTERLINGS))
    }
}
