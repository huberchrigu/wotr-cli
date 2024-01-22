package ch.chrigu.wotr.figure

enum class FigureType(val shortcut: String? = null, val isUnit: Boolean = false, val canBePartOfArmy: Boolean = true, val minion: Boolean = false) {
    WITCH_KING("wk", minion = true), SARUMAN("sa", minion = true), MOUTH_OF_SAURON("ms", minion = true),
    GANDALF_THE_WHITE("gw"), GANDALF_THE_GREY("gg"), STRIDER("st"), ARAGORN("ar"), BOROMIR("bo"), GIMLI("gi"), LEGOLAS("le"), MERIADOC("me"), PEREGRIN("pe"),
    REGULAR(isUnit = true), ELITE(isUnit = true), LEADER_OR_NAZGUL, FELLOWSHIP("fs", false, false);

    val isUniqueCharacter = shortcut != null

    override fun toString() = name.replace("_", " ").lowercase().replaceFirstChar { it.uppercaseChar() }

    companion object {
        fun fromShortcut(s: String) = entries.first { it.shortcut.equals(s, true) }
    }
}
