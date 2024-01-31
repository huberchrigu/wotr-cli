package ch.chrigu.wotr.figure

enum class FigureType(val shortcut: String? = null, val isUnit: Boolean = false, val canBePartOfArmy: Boolean = true, val minion: Boolean = false, val leadership: Int = 0) {
    WITCH_KING("wk", minion = true, leadership = 2), SARUMAN("sa", minion = true, leadership = 1), MOUTH_OF_SAURON("ms", minion = true, leadership = 2),
    GANDALF_THE_WHITE("gw", leadership = 1), GANDALF_THE_GREY("gg", leadership = 1), STRIDER("st", leadership = 1), ARAGORN("ar", leadership = 2),
    BOROMIR("bo", leadership = 1), GIMLI("gi", leadership = 1), LEGOLAS("le", leadership = 1), MERIADOC("me", leadership = 1), PEREGRIN("pe", leadership = 1),
    REGULAR(isUnit = true), ELITE(isUnit = true), LEADER_OR_NAZGUL(leadership = 1), FELLOWSHIP("fs", false, false);

    val isUniqueCharacter = shortcut != null

    override fun toString() = name.replace("_", " ").lowercase().replaceFirstChar { it.uppercaseChar() }

    companion object {
        fun fromShortcut(s: String) = entries.first { it.shortcut.equals(s, true) }
    }
}
