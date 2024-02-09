package ch.chrigu.wotr.location

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class LocationFinderTest {
    @ParameterizedTest
    @CsvSource(
        "RIVENDELL, MORANNON, MORIA, 10",
        "RIVENDELL, RIVENDELL, , 0",
        "MORIA, HOLLIN, HOLLIN, 1"
    )
    fun `should find paths`(from: LocationName, to: LocationName, through: LocationName?, length: Int) {
        val result = LocationFinder.getShortestPath(from, to)
        assertThat(result.map { it.getLength() }).containsOnly(length)
        if (through != null) {
            assertThat(result).allMatch { it.locations.contains(through) }
        }
    }
}
