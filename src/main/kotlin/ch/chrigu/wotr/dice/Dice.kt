package ch.chrigu.wotr.dice

import ch.chrigu.wotr.gamestate.GameState
import ch.chrigu.wotr.player.Player
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Dice(val shadow: DiceAndRings = DiceAndRings(emptyList(), 0, Player.SHADOW), val freePeople: DiceAndRings = DiceAndRings(emptyList(), 3, Player.FREE_PEOPLE)) {
    fun useDie(dieUsage: DieUsage) = copy(
        shadow = if (dieUsage.player == Player.SHADOW) shadow.use(dieUsage) else shadow,
        freePeople = if (dieUsage.player == Player.FREE_PEOPLE) freePeople.use(dieUsage) else freePeople
    )

    fun assignEyesAndRollDice(state: GameState, numEyes: Int) = copy(
        shadow = shadow.assignAndRoll(7 + state.numMinions(), numEyes),
        freePeople = freePeople.roll(4 + listOf(state.hasAragorn(), state.hasGandalfTheWhite()).sumOf { toInt(it) })
    )

    private fun toInt(it: Boolean) = if (it) 1 else 0

    init {
        require(!shadow.rolled.contains(DieType.WILL_OF_THE_WEST))
        require(!freePeople.rolled.contains(DieType.EYE))
    }
}

@Serializable
data class DiceAndRings(val rolled: List<DieType>, val rings: Int, val player: Player, val ringsUsed: Boolean = false, val huntBox: List<DieType> = emptyList()) {
    init {
        require(rings in 0..3)
    }

    fun isEmpty() = rolled.isEmpty()

    fun use(dieUsage: DieUsage): DiceAndRings {
        check(!dieUsage.useRing || !noRings())
        return copy(rolled = removeOneDie(dieUsage.use), rings = if (dieUsage.useRing) rings - 1 else rings, ringsUsed = if (dieUsage.useRing) true else ringsUsed)
    }

    fun roll(num: Int) = copy(rolled = (0 until num).map { rollDie() }, ringsUsed = false, huntBox = emptyList())

    fun assignAndRoll(num: Int, numEyes: Int): DiceAndRings {
        val roll = (0 until num - numEyes).map { rollDie() }
        val rolledEyes = roll.filter { it == DieType.EYE }
        return copy(rolled = roll - rolledEyes.toSet(), ringsUsed = false, huntBox = (0 until numEyes).map { DieType.EYE } + rolledEyes)
    }

    fun getDice(types: Set<DieType>): List<DieUsage> {
        val matchingDice = rolled.filter { types.contains(it) || it == DieType.WILL_OF_THE_WEST }
        val withRing = if (noRings()) emptyList() else (rolled - matchingDice.toSet()).map { DieUsage(it, true, player) }
        return matchingDice.map { DieUsage(it, false, player) } + withRing
    }

    override fun toString() = (rolled + (0 until rings).map { "X" }).joinToString(" ") + huntBoxToString()

    private fun removeOneDie(type: DieType): List<DieType> {
        val useIndex = rolled.indexOf(type)
        return rolled.subList(0, useIndex) + rolled.subList(useIndex + 1, rolled.size)
    }

    private fun huntBoxToString() = if (huntBox.isEmpty()) "" else " / " + huntBox.joinToString(" ")

    private fun rollDie() = player.dieFace[Random.nextInt(6)]
    private fun noRings() = rings == 0 || ringsUsed
}

enum class DieType(private val shortName: String? = null) {
    ARMY, MUSTER, ARMY_MUSTER("AM"), EYE, WILL_OF_THE_WEST, EVENT("P"), CHARACTER;

    override fun toString() = shortName ?: name.take(1)
}

data class DieUsage(val use: DieType, val useRing: Boolean, val player: Player) {
    override fun toString() = if (useRing) "ring" else use.toString()
}
