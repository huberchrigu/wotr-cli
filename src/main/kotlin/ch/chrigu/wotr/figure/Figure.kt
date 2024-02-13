package ch.chrigu.wotr.figure

import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.player.Player

class Figure(val type: FigureType, val nation: NationName) {
    init {
        if (type == FigureType.LEADER_OR_NAZGUL) {
            require(nation.player == Player.FREE_PEOPLE || nation == NationName.SAURON)
        }
    }

    val isFreePeopleLeader: Boolean
        get() = type == FigureType.LEADER_OR_NAZGUL && nation.player == Player.FREE_PEOPLE

    val isNazgul: Boolean
        get() = type == FigureType.LEADER_OR_NAZGUL && nation.player == Player.SHADOW

    fun isNazgulOrWitchKing() = isNazgul || type == FigureType.WITCH_KING

    fun isCharacterOrNazgul() = isNazgul || type.isUniqueCharacter
    override fun toString() = "${getTypeString()} ($nation)"

    private fun getTypeString() = if (type == FigureType.LEADER_OR_NAZGUL) {
        if (nation.player == Player.FREE_PEOPLE) "Leader" else "Nazgul"
    } else type.toString()
}
