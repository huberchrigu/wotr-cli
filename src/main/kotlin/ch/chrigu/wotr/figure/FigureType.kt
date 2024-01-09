package ch.chrigu.wotr.figure

enum class FigureType(val shortcut: String? = null, val isUnit: Boolean = false) {
    WITCH_KING("wk"), SARUMAN("sa"), MOUTH_OF_SAURON("ms"),
    GANDALF_THE_WHITE("gw"), GANDALF_THE_GREY("gg"), STRIDER("st"), ARAGORN("ar"), BOROMIR("bo"), GIMLI("gi"), LEGOLAS("le"), MERIADOC("me"), PEREGRIN("pe"),
    REGULAR(isUnit = true), ELITE(isUnit = true), LEADER_OR_NAZGUL;

    val isUniqueCharacter = shortcut != null

    companion object {
        fun fromShortcut(s: String) = entries.first { it.shortcut.equals(s, true) }
    }
}
