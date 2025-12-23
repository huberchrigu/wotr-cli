package ch.chrigu.wotr.action

import ch.chrigu.wotr.bot.dsl.given
import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.gamestate.GameStateFactory
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AttackActionTest {
    @Test
    fun `should retreat`() {
        val attackerLocation = LocationName.OSGILIATH
        val defenderLocation = LocationName.MINAS_TIRITH
        lateinit var attacker: List<Figure>
        lateinit var defender: List<Figure>

        given(GameStateFactory.newGame()) {
            nation(NationName.SAURON).atWar()
            remove("200") from attackerLocation
            move("501") from LocationName.MINAS_MORGUL to attackerLocation
        } expect {
            attacker = gameState.location[attackerLocation]!!.allFigures
            defender = gameState.location[defenderLocation]!!.allFigures
            attack("501") from attackerLocation to defenderLocation
        } toResultIn {
            assertThat(it.vpShadow()).isEqualTo(0)
            val location = it.location[defenderLocation]
            assertThat(location?.captured).isFalse
            assertThat(location?.nonBesiegedFigures?.army).isEqualTo(attacker)
            assertThat(location?.besiegedFigures?.army).isEqualTo(defender)
        }
    }
}