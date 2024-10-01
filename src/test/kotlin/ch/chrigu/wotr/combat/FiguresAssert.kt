package ch.chrigu.wotr.combat

import ch.chrigu.wotr.figure.Figures
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
}

fun assertThat(actual: Figures) = FiguresAssert(actual)
fun Figures.assert() = FiguresAssert(this)
