package ch.chrigu.wotr.gamestate

import ch.chrigu.wotr.figure.Figure
import ch.chrigu.wotr.figure.FigureType
import ch.chrigu.wotr.figure.Figures
import ch.chrigu.wotr.figure.FiguresType
import ch.chrigu.wotr.location.Location
import ch.chrigu.wotr.location.LocationName
import ch.chrigu.wotr.location.LocationName.*
import ch.chrigu.wotr.nation.Nation
import ch.chrigu.wotr.nation.NationName
import ch.chrigu.wotr.nation.NationName.*
import ch.chrigu.wotr.player.Player

object GameStateFactory {

    private val initFigures = mapOf(
        f(ERED_LUIN, 1, 0, 0),
        f(EREBOR, 1, 2, 1),
        f(IRON_HILLS, 1, 0, 0),
        f(THE_SHIRE, 1, 0, 0),
        f(NORTH_DOWNS, 0, 1, 0),
        f(DALE, 1, 0, 1),
        f(CARROCK, 1, 0, 0),
        f(BREE, 1, 0, 0),
        f(GREY_HAVENS, 1, 1, 1),
        f(RIVENDELL, 0, 2, 1),
        f(WOODLAND_REALM, 1, 1, 1),
        f(LORIEN, 1, 2, 1),
        f(EDORAS, 1, 1, 0),
        f(FORDS_OF_ISEN, 2, 0, 1),
        f(HELMS_DEEP, 1, 0, 0),
        f(MINAS_TIRITH, 3, 1, 1),
        f(DOL_AMROTH, 3, 0, 0),
        f(OSGILIATH, 2, 0, 0, GONDOR),
        f(PELARGIR, 1, 0, 0),
        f(ORTHANC, 4, 1, 0),
        f(NORTH_DUNLAND, 1, 0, 0),
        f(SOUTH_DUNLAND, 1, 0, 0),
        f(FAR_HARAD, 3, 1, 0),
        f(NEAR_HARAD, 3, 1, 0),
        f(NORTH_RHUN, 2, 0, 0),
        f(SOUTH_RHUN, 3, 1, 0),
        f(UMBAR, 3, 0, 0),
        f(BARAD_DUR, 4, 1, 1),
        f(DOL_GULDUR, 5, 1, 1),
        f(GORGOROTH, 3, 0, 0),
        f(MINAS_MORGUL, 5, 0, 1),
        f(MORIA, 2, 0, 0),
        f(MOUNT_GUNDABAD, 2, 0, 0),
        f(NURN, 2, 0, 0),
        f(MORANNON, 5, 0, 1)
    )

    fun newGame() = GameState(LocationName.entries.associateWith { getLocation(it) }, createNations(), createReinforcements(), createCompanions())

    private fun createNations() = NationName.entries.filter { it != FREE_PEOPLE }
        .associateWith { Nation(getBoxNumber(it), getNationActive(it), it) }

    private fun getBoxNumber(nationName: NationName) = when (nationName) {
        SAURON, ISENGARD -> 1
        GONDOR, SOUTHRONS_AND_EASTERLINGS -> 2
        else -> 3
    }

    private fun getNationActive(nationName: NationName) = nationName.player == Player.SHADOW || nationName == ELVES

    private fun getLocation(location: LocationName) = Location(location, getFigures(location))

    private fun getFigures(name: LocationName): Figures {
        if (initFigures.containsKey(name)) {
            val notNullNation: NationName = initFigures[name]!!.second ?: name.nation!!
            val all = initFigures[name]!!.first
                .map { Figure(it, notNullNation) }
            val fs = if (name == RIVENDELL) listOf(Figure(FigureType.FELLOWSHIP, FREE_PEOPLE)) else emptyList()
            return Figures(all + fs)
        } else {
            return Figures(emptyList())
        }
    }

    private fun createReinforcements() = Figures(
        createFiguresFromNationName(DWARVES, 2, 3, 3) +
                createFiguresFromNationName(ELVES, 2, 4) +
                createFiguresFromNationName(GONDOR, 6, 4, 3) +
                createFiguresFromNationName(NORTHMEN, 6, 4, 3) +
                createFiguresFromNationName(ROHAN, 6, 4, 3) +
                createFiguresFromNationName(ISENGARD, 6, 5) +
                createFiguresFromNationName(SAURON, 8, 4, 4) +
                createFiguresFromNationName(SOUTHRONS_AND_EASTERLINGS, 10, 3) +
                listOf(Figure(FigureType.WITCH_KING, SAURON), Figure(FigureType.MOUTH_OF_SAURON, SAURON), Figure(FigureType.SARUMAN, ISENGARD)) +
                listOf(Figure(FigureType.ARAGORN, FREE_PEOPLE), Figure(FigureType.GANDALF_THE_WHITE, FREE_PEOPLE)),
        FiguresType.POOL
    )

    private fun createCompanions() = listOf(
        Figure(FigureType.GANDALF_THE_GREY, FREE_PEOPLE),
        Figure(FigureType.STRIDER, NORTHMEN),
        Figure(FigureType.GIMLI, DWARVES),
        Figure(FigureType.LEGOLAS, ELVES),
        Figure(FigureType.BOROMIR, GONDOR),
        Figure(FigureType.PEREGRIN, FREE_PEOPLE),
        Figure(FigureType.MERIADOC, FREE_PEOPLE)
    )

    private fun f(name: LocationName, numRegular: Int, numElite: Int, numLeaderOrNazgul: Int, nation: NationName? = null) =
        name to Pair(createFigures(numRegular, numElite, numLeaderOrNazgul), nation)

    private fun createFiguresFromNationName(nation: NationName, numRegular: Int, numElite: Int, numLeaderOrNazgul: Int = 0) =
        createFigures(numRegular, numElite, numLeaderOrNazgul)
            .map { Figure(it, nation) }

    private fun createFigures(numRegular: Int, numElite: Int, numLeaderOrNazgul: Int) = (0 until numRegular).map { FigureType.REGULAR } +
            (0 until numElite).map { FigureType.ELITE } +
            (0 until numLeaderOrNazgul).map { FigureType.LEADER_OR_NAZGUL }
}
