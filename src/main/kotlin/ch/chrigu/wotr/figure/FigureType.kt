package ch.chrigu.wotr.figure

enum class FigureType(val shortcut: String? = null) {
    WITCH_KING("wk"), SARUMAN("sa"), MOUTH_OF_SAURON("ms"),
    GANDALF_THE_WHITE("gw"), GANDALF_THE_GREY("gg"), STRIDER("st"), ARAGORN("ar"), BOROMIR("bo"), GIMLI("gi"), LEGOLAS("le"), MERIADOC("me"), PEREGRIN("pe"),
    REGULAR, ELITE, LEADER;

    companion object {
        fun fromShortcut(s: String) = entries.first { it.shortcut.equals(s, true) }
    }
}
