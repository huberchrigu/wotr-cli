package ch.chrigu.wotr.figure

enum class FiguresType(val figureLimit: Int) {
    POOL(Integer.MAX_VALUE), LOCATION(FigureLimit.STANDARD), BESIEGED(FigureLimit.STRONGHOLD)
}
