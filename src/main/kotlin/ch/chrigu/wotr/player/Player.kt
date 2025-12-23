package ch.chrigu.wotr.player

import ch.chrigu.wotr.dice.DieType

enum class Player(val dieFace: List<DieType>) {
    SHADOW(listOf(DieType.ARMY, DieType.ARMY_MUSTER, DieType.MUSTER, DieType.EYE, DieType.EVENT, DieType.CHARACTER)),
    FREE_PEOPLE(listOf(DieType.ARMY, DieType.ARMY_MUSTER, DieType.MUSTER, DieType.WILL_OF_THE_WEST, DieType.EVENT, DieType.CHARACTER));

    val opponent by lazy { entries.first { it != this } }
}
