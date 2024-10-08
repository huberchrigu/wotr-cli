package ch.chrigu.wotr.combat

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.toFigures
import ch.chrigu.wotr.nation.NationName
import org.assertj.core.api.AbstractAssert

class FiguresAssert(actual: Figures) : AbstractAssert<FiguresAssert, Figures>(actual, FiguresAssert::class.java) {
    fun hasArmy(numRegulars: Int, numElites: Int, numLeadersOrNazgul: Int): FiguresAssert {
        if (actual.numRegulars() != numRegulars) {
            failWithMessage("Expected <$numRegulars> regulars, but has <${actual.numRegulars()}>")
        } else if (actual.numElites() != numElites) {
            failWithMessage("Expected <$numElites> elites, but has <${actual.numElites()}>")
        } else if (actual.numLeadersOrNazgul() != numLeadersOrNazgul) {
            failWithMessage("Expected <$numLeadersOrNazgul> leaders/nazguls, but has <${actual.numLeadersOrNazgul()}>")
        }
        return this
    }

    fun hasArmy(numRegulars: Int, numElites: Int, numLeadersOrNazgul: Int, nation: NationName): FiguresAssert {
        if (actual.all.count { it.nation == nation && it.type == FigureType.REGULAR } != numRegulars) {
            failWithMessage("Expected <$numRegulars> regulars, but has <${actual.numRegulars()}>")
        } else if (actual.all.count { it.nation == nation && it.type == FigureType.ELITE } != numElites) {
            failWithMessage("Expected <$numElites> elites, but has <${actual.numElites()}>")
        } else if (actual.all.count { it.nation == nation && it.type == FigureType.LEADER_OR_NAZGUL } != numLeadersOrNazgul) {
            failWithMessage("Expected <$numLeadersOrNazgul> leaders/nazguls, but has <${actual.numLeadersOrNazgul()}>")
        }
        return this
    }

    fun isEmpty(): FiguresAssert {
        if (!actual.isEmpty()) failWithMessage("Expected empty, but was <$actual>")
        return this
    }
}

fun assertThat(actual: Figures) = FiguresAssert(actual)
fun List<Figure>.assert() = FiguresAssert(this.toFigures())
fun Figures.assert() = FiguresAssert(this)
