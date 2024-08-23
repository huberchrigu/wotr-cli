package ch.chrigu.wotr.bot.dsl

import ch.chrigu.wotr.action.AttackAction
import ch.chrigu.wotr.bot.BotEvaluationService
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.mock

fun given(gameState: GameState, apply: GivenDsl.() -> Unit) = GivenDsl(gameState).apply(apply)
class GivenDsl(var gameState: GameState) {
    fun nation(name: NationName) = NationDsl(this, name)
    fun remove(units: String) = RemoveDsl(this, units)
    fun move(units: String) = UnitActionDsl(this, units) { from, to, figures -> removeFrom(from, figures).addTo(to, figures) }
    fun attack(units: String) = UnitActionDsl(this, units) { from, to, figures -> AttackAction.create(mock(), this, from, to, figures).simulate(this) }

    infix fun expect(apply: GivenDsl.() -> Unit) = ExpectedDsl(gameState, apply)
}

class ExpectedDsl(private val gameState: GameState, private val expected: GivenDsl.() -> Unit) {
    infix fun toBeBetterThan(apply: GivenDsl.() -> Unit) {
        val better = GivenDsl(gameState).apply(expected).gameState
        val worse = GivenDsl(gameState).apply(apply).gameState
        assertThat(BotEvaluationService.count(better)).isGreaterThan(BotEvaluationService.count(worse))
    }
}
