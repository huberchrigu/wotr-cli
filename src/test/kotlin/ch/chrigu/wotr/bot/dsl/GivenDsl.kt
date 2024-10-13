package ch.chrigu.wotr.bot.dsl

import ch.chrigu.wotr.action.AttackAction
import ch.chrigu.wotr.bot.BotEvaluationService
import ch.chrigu.wotr.gamestate.At
import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.mock

fun given(gameState: GameState, apply: GivenDsl.() -> Unit) = GivenDsl(gameState).apply(apply)
class GivenDsl(override var gameState: GameState) : GameStateProvider {
    fun nation(name: NationName) = NationDsl(this, name)
    fun remove(units: String) = RemoveDsl(this, units)
    fun move(units: String) = UnitActionDsl(this, units) { from, to, figures -> move(figures, At(from), At(to)) }
    fun reinforce(units: String) = ReinforceDsl(this, units)
    fun attack(units: String) = UnitActionDsl(this, units) { from, to, figures -> AttackAction.create(mock(), this, from, to, figures).simulate(this) }
    infix fun expect(apply: GivenDsl.() -> Unit) = ExpectedDsl(gameState, apply)
}

class ExpectedDsl(private val gameState: GameState, private val expected: GivenDsl.() -> Unit) {
    infix fun toBeBetterThan(apply: GivenDsl.() -> Unit) {
        val better = GivenDsl(gameState).apply(expected).gameState
        val worse = GivenDsl(gameState).apply(apply).gameState
        assertThat(BotEvaluationService.count(better)).describedAs("Diff better state to worse:\n${better.diff(worse)}").isGreaterThan(BotEvaluationService.count(worse))
    }
}
