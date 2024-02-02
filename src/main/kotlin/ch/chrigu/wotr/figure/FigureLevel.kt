package ch.chrigu.wotr.figure

sealed interface FigureLevel
class NumberedLevel(val number: Int) : FigureLevel
data object FlyingLevel : FigureLevel
