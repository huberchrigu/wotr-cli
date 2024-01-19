package ch.chrigu.wotr.dice

import ch.chrigu.wotr.player.Player

data class Dice(val shadow: DiceAndRings = DiceAndRings(emptyList(), 0, Player.SHADOW), val freePeople: DiceAndRings = DiceAndRings(emptyList(), 3, Player.FREE_PEOPLE)) {
    fun useDie(dieUsage: DieUsage) = copy(
        shadow = if (dieUsage.player == Player.SHADOW) shadow.use(dieUsage) else shadow,
        freePeople = if (dieUsage.player == Player.FREE_PEOPLE) freePeople.use(dieUsage) else freePeople
    )

    init {
        require(!shadow.rolled.contains(DieType.WILL_OF_THE_WEST))
        require(!freePeople.rolled.contains(DieType.EYE))
    }
}

data class DiceAndRings(val rolled: List<DieType>, val rings: Int, val player: Player, val ringsUsed: Boolean = false) {
    init {
        require(rings in 0..3)
    }

    fun isEmpty() = rolled.isEmpty()
    fun getDiceToPlayCharacterEvent(): List<DieUsage> {
        val matchingDice = rolled.filter { it == DieType.EVENT || it == DieType.CHARACTER || it == DieType.WILL_OF_THE_WEST }
        val withRing = if (noRings()) emptyList() else (rolled - matchingDice.toSet()).map { DieUsage(it, true, player) }
        return matchingDice.map { DieUsage(it, false, player) } + withRing
    }

    fun use(dieUsage: DieUsage): DiceAndRings {
        check(!dieUsage.useRing || !noRings())
        return copy(rolled = rolled - listOf(dieUsage.use).toSet(), rings = if (dieUsage.useRing) rings - 1 else rings, ringsUsed = if (dieUsage.useRing) true else ringsUsed)
    }

    private fun noRings() = rings == 0 || ringsUsed
}

enum class DieType { ARMY, MUSTER, ARMY_MUSTER, EYE, WILL_OF_THE_WEST, EVENT, CHARACTER }

data class DieUsage(val use: DieType, val useRing: Boolean, val player: Player)
