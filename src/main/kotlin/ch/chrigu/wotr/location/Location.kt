package ch.chrigu.wotr.location

import ch.chrigu.wotr.figure.Figures

data class Location(
    val nonBesiegedFigures: Figures = Figures(emptyList())
) {

    fun remove(figures: Figures) = copy(nonBesiegedFigures = nonBesiegedFigures - figures)

    fun add(figures: Figures) = copy(nonBesiegedFigures = nonBesiegedFigures + figures)

    override fun toString() = nonBesiegedFigures.toString()
}
