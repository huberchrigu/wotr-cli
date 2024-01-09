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
}
