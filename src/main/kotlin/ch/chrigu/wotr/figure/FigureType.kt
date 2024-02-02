package ch.chrigu.wotr.figure

enum class FigureType(
    val shortcut: String? = null,
    val isUnit: Boolean = false,
    val canBePartOfArmy: Boolean = true,
    val minion: Boolean = false,
    val leadership: Int = 0,
    val level: FigureLevel? = null,
    val enablesDice: Int = 0
) {
    WITCH_KING("wk", minion = true, leadership = 2, level = FlyingLevel, enablesDice = 1),
    SARUMAN("sa", minion = true, leadership = 1, level = NumberedLevel(0), enablesDice = 1),
    MOUTH_OF_SAURON("ms", minion = true, leadership = 2, level = NumberedLevel(3), enablesDice = 1),
    GANDALF_THE_WHITE("gw", leadership = 1, level = NumberedLevel(3), enablesDice = 1),
    GANDALF_THE_GREY("gg", leadership = 1, level = NumberedLevel(3)),
    STRIDER("st", leadership = 1, level = NumberedLevel(3)),
    ARAGORN("ar", leadership = 2, level = NumberedLevel(3), enablesDice = 1),
    BOROMIR("bo", leadership = 1, level = NumberedLevel(2)),
    GIMLI("gi", leadership = 1, level = NumberedLevel(2)),
    LEGOLAS("le", leadership = 1, level = NumberedLevel(2)),
    MERIADOC("me", leadership = 1, level = NumberedLevel(1)),
    PEREGRIN("pe", leadership = 1, level = NumberedLevel(1)),
    REGULAR(isUnit = true),
    ELITE(isUnit = true),
    LEADER_OR_NAZGUL(leadership = 1),
    FELLOWSHIP("fs", canBePartOfArmy = false);

    val isUniqueCharacter = shortcut != null

    override fun toString() = name.replace("_", " ").lowercase().replaceFirstChar { it.uppercaseChar() }

    companion object {
        fun fromShortcut(s: String) = entries.first { it.shortcut.equals(s, true) }
    }
}
