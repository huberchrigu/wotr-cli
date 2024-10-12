package ch.chrigu.wotr.location

import ch.chrigu.wotr.nation.NationName

enum class LocationName(val shortcut: String, val adjacent: () -> List<LocationName>, val nation: NationName? = null, val type: LocationType = LocationType.NONE) {
    ANDRAST("and", { listOf(DRUWAITH_IAUR, ANFALAS) }),
    ANFALAS("anf", { listOf(ANDRAST, ERECH, DOL_AMROTH) }, NationName.GONDOR),
    ANGMAR("ang", { listOf(ARNOR, ETTENMOORS, MOUNT_GRAM) }, NationName.SAURON, LocationType.CITY),
    ARNOR("arn", { listOf(EVENDIM, NORTH_DOWNS, ETTENMOORS, ANGMAR) }),
    ASH_MOUNTAINS("am", { listOf(SOUTHERN_DORWINION, NOMAN_LANDS, DAGORLAD, SOUTH_RHUN) }),
    BARAD_DUR("bd", { listOf(GORGOROTH) }, NationName.SAURON, LocationType.STRONGHOLD),
    BREE("br", { listOf(NORTH_DOWNS, BUCKLAND, SOUTH_DOWNS, WEATHER_HILLS) }, NationName.NORTHMEN, LocationType.VILLAGE),
    BUCKLAND("bu", { listOf(THE_SHIRE, OLD_FOREST, CARDOLAN, SOUTH_DOWNS, BREE, NORTH_DOWNS, EVENDIM) }, NationName.NORTHMEN),
    CARDOLAN("cn", { listOf(OLD_FOREST, SOUTH_ERED_LUIN, MINHIRIATH, THARBAD, NORTH_DUNLAND, SOUTH_DOWNS, BUCKLAND) }),
    CARROCK("ck", { listOf(EAGLES_EYRIE, OLD_FORD, RHOSGOBEL, OLD_FOREST_ROAD, WESTERN_MIRKWOOD, NORTHERN_MIRKWOOD) }, NationName.NORTHMEN, LocationType.VILLAGE),
    DAGORLAD("dad", { listOf(NOMAN_LANDS, EASTERN_EMYN_MUIL, NORTH_ITHILIEN, MORANNON, ASH_MOUNTAINS) }),
    DALE("da", { listOf(EREBOR, WITHERED_HEATH, WOODLAND_REALM, OLD_FOREST_ROAD, NORTHERN_RHOVANION, VALE_OF_THE_CARNEN, IRON_HILLS) }, NationName.NORTHMEN, LocationType.CITY),
    DEAD_MARSHES("dm", { listOf(EASTERN_EMYN_MUIL, WESTERN_EMYN_MUIL, DRUADAN_FOREST, OSGILIATH, NORTH_ITHILIEN) }),
    DIMRILL_DALE("dd", { listOf(GLADDEN_FIELDS, MORIA, LORIEN, PARTH_CELEBRANT, SOUTH_ANDUIN_VALE, NORTH_ANDUIN_VALE) }),
    DOL_AMROTH("dam", { listOf(ANFALAS, ERECH, LAMEDON) }, NationName.GONDOR, LocationType.STRONGHOLD),
    DOL_GULDUR(
        "dg",
        { listOf(NARROWS_OF_THE_FOREST, NORTH_ANDUIN_VALE, SOUTH_ANDUIN_VALE, WESTERN_BROWN_LANDS, EASTERN_BROWN_LANDS, SOUTHERN_MIRKWOOD, EASTERN_MIRKWOOD) },
        NationName.SAURON,
        LocationType.STRONGHOLD
    ),
    DRUADAN_FOREST("df", { listOf(EASTEMNET, FOLDE, MINAS_TIRITH, OSGILIATH, DEAD_MARSHES, WESTERN_EMYN_MUIL) }, NationName.GONDOR),
    DRUWAITH_IAUR("di", { listOf(ENEDWAITH, ANDRAST, FORDS_OF_ISEN, GAP_OF_ROHAN) }),
    EAGLES_EYRIE("ee", { listOf(MOUNT_GUNDABAD, OLD_FORD, CARROCK) }),
    EASTEMNET("ea", { listOf(PARTH_CELEBRANT, FANGORN, WESTEMNET, FOLDE, DRUADAN_FOREST, WESTERN_EMYN_MUIL, WESTERN_BROWN_LANDS) }, NationName.ROHAN),
    EASTERN_BROWN_LANDS("ebl", { listOf(SOUTHERN_MIRKWOOD, DOL_GULDUR, WESTERN_BROWN_LANDS, WESTERN_EMYN_MUIL, EASTERN_EMYN_MUIL, NOMAN_LANDS, SOUTHERN_RHOVANION) }),
    EASTERN_EMYN_MUIL("eem", { listOf(EASTERN_BROWN_LANDS, WESTERN_EMYN_MUIL, DEAD_MARSHES, NORTH_ITHILIEN, DAGORLAD, NOMAN_LANDS) }),
    EASTERN_MIRKWOOD("em", { listOf(OLD_FOREST_ROAD, NARROWS_OF_THE_FOREST, DOL_GULDUR, SOUTHERN_MIRKWOOD, NORTHERN_RHOVANION) }),
    EAST_HARONDOR("eh", { listOf(SOUTH_ITHILIEN, WEST_HARONDOR, NEAR_HARAD) }),
    EAST_RHUN("erh", { listOf(IRON_HILLS, VALE_OF_THE_CARNEN, NORTH_RHUN, SOUTH_RHUN) }, NationName.SOUTHRONS_AND_EASTERLINGS),
    EDORAS("ed", { listOf(WESTEMNET, FOLDE) }, NationName.ROHAN, LocationType.CITY),
    ENEDWAITH("en", { listOf(THARBAD, MINHIRIATH, DRUWAITH_IAUR, GAP_OF_ROHAN, SOUTH_DUNLAND) }),
    EREBOR(
        "eb",
        { listOf(WITHERED_HEATH, DALE, IRON_HILLS) }, NationName.DWARVES, LocationType.STRONGHOLD
    ),
    ERECH(
        "ere",
        { listOf(ANFALAS, DOL_AMROTH, LAMEDON) }, NationName.GONDOR
    ),
    ERED_LUIN(
        "el",
        { listOf(FORLINDON, GREY_HAVENS, TOWER_HILLS, NORTH_ERED_LUIN, EVENDIM) }, NationName.DWARVES, LocationType.VILLAGE
    ),
    ETTENMOORS("et",
        { listOf(ANGMAR, ARNOR, NORTH_DOWNS, WEATHER_HILLS, TROLLSHAWS, MOUNT_GRAM) }),
    EVENDIM("ev",
        { listOf(NORTH_ERED_LUIN, ERED_LUIN, TOWER_HILLS, THE_SHIRE, BUCKLAND, NORTH_DOWNS, ARNOR) }),
    FANGORN("fa",
        { listOf(PARTH_CELEBRANT, FORDS_OF_ISEN, WESTEMNET, EASTEMNET) }),
    FAR_HARAD(
        "fh",
        { listOf(KHAND, NEAR_HARAD) }, NationName.SOUTHRONS_AND_EASTERLINGS, LocationType.CITY
    ),
    FOLDE(
        "fol",
        { listOf(EASTEMNET, WESTEMNET, EDORAS, DRUADAN_FOREST) }, NationName.ROHAN, LocationType.VILLAGE
    ),
    FORDS_OF_BRUINEN("fb",
        { listOf(RIVENDELL, TROLLSHAWS, HOLLIN, HIGH_PASS) }),
    FORDS_OF_ISEN(
        "fi",
        { listOf(ORTHANC, GAP_OF_ROHAN, DRUWAITH_IAUR, HELMS_DEEP, WESTEMNET, FANGORN) }, NationName.ROHAN, LocationType.FORTIFICATION
    ),
    FORLINDON("for",
        { listOf(NORTH_ERED_LUIN, ERED_LUIN, GREY_HAVENS) }),
    GAP_OF_ROHAN(
        "gr",
        { listOf(SOUTH_DUNLAND, ENEDWAITH, DRUWAITH_IAUR, FORDS_OF_ISEN, ORTHANC) }, NationName.ISENGARD
    ),
    GLADDEN_FIELDS("gf",
        { listOf(OLD_FORD, DIMRILL_DALE, RHOSGOBEL, NORTH_ANDUIN_VALE) }),
    GOBLINS_GATE("gg",
        { listOf(HIGH_PASS, OLD_FORD) }),
    GORGOROTH(
        "go",
        { listOf(BARAD_DUR, MORANNON, MINAS_MORGUL, NURN) }, NationName.SAURON
    ),
    GREY_HAVENS(
        "gh",
        { listOf(FORLINDON, HARLINDON, TOWER_HILLS, ERED_LUIN) }, NationName.ELVES, LocationType.STRONGHOLD
    ),
    HARLINDON("ha",
        { listOf(GREY_HAVENS, SOUTH_ERED_LUIN) }),
    HELMS_DEEP(
        "hd",
        { listOf(FORDS_OF_ISEN, WESTEMNET) }, NationName.ROHAN, LocationType.STRONGHOLD
    ),
    HIGH_PASS("hp",
        { listOf(FORDS_OF_BRUINEN, GOBLINS_GATE) }),
    HOLLIN("ho",
        { listOf(FORDS_OF_BRUINEN, TROLLSHAWS, SOUTH_DOWNS, NORTH_DUNLAND, MORIA) }),
    IRON_HILLS(
        "ih",
        { listOf(EREBOR, DALE, VALE_OF_THE_CARNEN, EAST_RHUN) }, NationName.DWARVES, LocationType.VILLAGE
    ),
    KHAND(
        "kh",
        { listOf(NEAR_HARAD, FAR_HARAD) }, NationName.SOUTHRONS_AND_EASTERLINGS
    ),
    LAMEDON(
        "la",
        { listOf(ERECH, DOL_AMROTH, PELARGIR) }, NationName.GONDOR, LocationType.VILLAGE
    ),
    LORIEN(
        "lor",
        { listOf(DIMRILL_DALE, PARTH_CELEBRANT) }, NationName.ELVES, LocationType.STRONGHOLD
    ),
    LOSSARNACH(
        "los",
        { listOf(MINAS_TIRITH, PELARGIR, OSGILIATH) }, NationName.GONDOR, LocationType.VILLAGE
    ),
    MINAS_MORGUL(
        "mm",
        { listOf(NORTH_ITHILIEN, SOUTH_ITHILIEN, GORGOROTH) }, NationName.SAURON, LocationType.STRONGHOLD
    ),
    MINAS_TIRITH(
        "mt",
        { listOf(DRUADAN_FOREST, LOSSARNACH, OSGILIATH) }, NationName.GONDOR, LocationType.STRONGHOLD
    ),
    MINHIRIATH("mi",
        { listOf(SOUTH_ERED_LUIN, ENEDWAITH, THARBAD, CARDOLAN) }),
    MORANNON(
        "mn",
        { listOf(DAGORLAD, GORGOROTH) }, NationName.SAURON, LocationType.STRONGHOLD
    ),
    MORIA(
        "mo",
        { listOf(HOLLIN, NORTH_DUNLAND, DIMRILL_DALE) }, NationName.SAURON, LocationType.STRONGHOLD
    ),
    MOUNT_GRAM(
        "mgr",
        { listOf(ANGMAR, ETTENMOORS, MOUNT_GUNDABAD) }, NationName.SAURON
    ),
    MOUNT_GUNDABAD(
        "mgu",
        { listOf(MOUNT_GRAM, EAGLES_EYRIE) }, NationName.SAURON, LocationType.STRONGHOLD
    ),
    NARROWS_OF_THE_FOREST("nf",
        { listOf(OLD_FOREST_ROAD, RHOSGOBEL, NORTH_ANDUIN_VALE, DOL_GULDUR, EASTERN_MIRKWOOD) }),
    NEAR_HARAD(
        "nh",
        { listOf(EAST_HARONDOR, WEST_HARONDOR, UMBAR, FAR_HARAD, KHAND) }, NationName.SOUTHRONS_AND_EASTERLINGS, LocationType.VILLAGE
    ),
    NOMAN_LANDS("nl",
        { listOf(SOUTHERN_RHOVANION, EASTERN_BROWN_LANDS, EASTERN_EMYN_MUIL, DAGORLAD, ASH_MOUNTAINS, SOUTHERN_DORWINION) }),
    NORTHERN_DORWINION("ndn",
        { listOf(VALE_OF_THE_CELDUIN, SOUTHERN_RHOVANION, SOUTHERN_DORWINION, NORTH_RHUN) }),
    NORTHERN_MIRKWOOD("nm",
        { listOf(CARROCK, WESTERN_MIRKWOOD, WOODLAND_REALM, WITHERED_HEATH) }),
    NORTHERN_RHOVANION("nrv",
        { listOf(DALE, OLD_FOREST_ROAD, EASTERN_MIRKWOOD, SOUTHERN_MIRKWOOD, SOUTHERN_RHOVANION, VALE_OF_THE_CELDUIN, VALE_OF_THE_CARNEN) }),
    NORTH_ANDUIN_VALE("nav",
        { listOf(RHOSGOBEL, GLADDEN_FIELDS, DIMRILL_DALE, SOUTH_ANDUIN_VALE, DOL_GULDUR, NARROWS_OF_THE_FOREST) }),
    NORTH_DOWNS(
        "nds",
        { listOf(ARNOR, EVENDIM, BUCKLAND, BREE, WEATHER_HILLS, ETTENMOORS) }, NationName.NORTHMEN
    ),
    NORTH_DUNLAND(
        "ndd",
        { listOf(MORIA, HOLLIN, SOUTH_DOWNS, CARDOLAN, THARBAD, SOUTH_DUNLAND) }, NationName.ISENGARD, LocationType.VILLAGE
    ),
    NORTH_ERED_LUIN(
        "nel",
        { listOf(FORLINDON, ERED_LUIN, EVENDIM) }, NationName.DWARVES
    ),
    NORTH_ITHILIEN("ni",
        { listOf(EASTERN_EMYN_MUIL, DEAD_MARSHES, OSGILIATH, SOUTH_ITHILIEN, MINAS_MORGUL, DAGORLAD) }),
    NORTH_RHUN(
        "nrh",
        {
            listOf(
                EAST_RHUN,
                VALE_OF_THE_CARNEN,
                VALE_OF_THE_CELDUIN,
                NORTHERN_DORWINION
            )
        }, NationName.SOUTHRONS_AND_EASTERLINGS, LocationType.VILLAGE
    ),
    NURN(
        "nu",
        { listOf(GORGOROTH) }, NationName.SAURON, LocationType.VILLAGE
    ),
    OLD_FORD("ofd",
        { listOf(EAGLES_EYRIE, GOBLINS_GATE, GLADDEN_FIELDS, RHOSGOBEL, CARROCK) }),
    OLD_FOREST("oft",
        { listOf(BUCKLAND, THE_SHIRE, SOUTH_ERED_LUIN, CARDOLAN) }),
    OLD_FOREST_ROAD(
        "ofr",
        {
            listOf(
                WOODLAND_REALM,
                WESTERN_MIRKWOOD,
                CARROCK,
                RHOSGOBEL,
                NARROWS_OF_THE_FOREST,
                EASTERN_MIRKWOOD,
                NORTHERN_RHOVANION,
                DALE
            )
        }, NationName.NORTHMEN
    ),
    ORTHANC(
        "or",
        { listOf(GAP_OF_ROHAN, FORDS_OF_ISEN) }, NationName.ISENGARD, LocationType.STRONGHOLD
    ),
    OSGILIATH(
        "os",
        {
            listOf(
                DEAD_MARSHES,
                DRUADAN_FOREST,
                MINAS_TIRITH,
                LOSSARNACH,
                PELARGIR,
                WEST_HARONDOR,
                SOUTH_ITHILIEN,
                NORTH_ITHILIEN
            )
        }, null, LocationType.FORTIFICATION
    ),
    PARTH_CELEBRANT("pc",
        { listOf(LORIEN, FANGORN, EASTEMNET, WESTERN_BROWN_LANDS, SOUTH_ANDUIN_VALE, DIMRILL_DALE) }),
    PELARGIR(
        "pe",
        { listOf(LAMEDON, WEST_HARONDOR, LOSSARNACH, OSGILIATH) }, NationName.GONDOR, LocationType.CITY
    ),
    RHOSGOBEL(
        "rh",
        { listOf(CARROCK, OLD_FORD, GLADDEN_FIELDS, NORTH_ANDUIN_VALE, NARROWS_OF_THE_FOREST, OLD_FOREST_ROAD) }, NationName.NORTHMEN
    ),
    RIVENDELL(
        "ri",
        { listOf(TROLLSHAWS, FORDS_OF_BRUINEN) }, NationName.ELVES, LocationType.STRONGHOLD
    ),
    SOUTHERN_DORWINION("sd",
        { listOf(NORTHERN_DORWINION, SOUTHERN_RHOVANION, NOMAN_LANDS, ASH_MOUNTAINS, SOUTH_RHUN) }),
    SOUTHERN_MIRKWOOD(
        "sm",
        { listOf(EASTERN_MIRKWOOD, DOL_GULDUR, EASTERN_BROWN_LANDS, SOUTHERN_RHOVANION, NORTHERN_RHOVANION) }, NationName.SAURON
    ),
    SOUTHERN_RHOVANION("srv",
        { listOf(NORTHERN_RHOVANION, SOUTHERN_MIRKWOOD, EASTERN_BROWN_LANDS, NOMAN_LANDS, SOUTHERN_DORWINION, NORTHERN_DORWINION, VALE_OF_THE_CELDUIN) }),
    SOUTH_ANDUIN_VALE("sav",
        { listOf(NORTH_ANDUIN_VALE, DIMRILL_DALE, PARTH_CELEBRANT, WESTERN_BROWN_LANDS, DOL_GULDUR) }),
    SOUTH_DOWNS("sdo",
        { listOf(WEATHER_HILLS, BREE, BUCKLAND, CARDOLAN, NORTH_DUNLAND, HOLLIN, TROLLSHAWS) }),
    SOUTH_DUNLAND(
        "sdu",
        { listOf(NORTH_DUNLAND, THARBAD, ENEDWAITH, GAP_OF_ROHAN) }, NationName.ISENGARD, LocationType.VILLAGE
    ),
    SOUTH_ERED_LUIN("sel",
        { listOf(THE_SHIRE, TOWER_HILLS, HARLINDON, MINHIRIATH, CARDOLAN, OLD_FOREST) }),
    SOUTH_ITHILIEN("si",
        { listOf(NORTH_ITHILIEN, OSGILIATH, WEST_HARONDOR, EAST_HARONDOR, MINAS_MORGUL) }),
    SOUTH_RHUN(
        "srh",
        { listOf(EAST_RHUN, SOUTHERN_DORWINION, ASH_MOUNTAINS) }, NationName.SOUTHRONS_AND_EASTERLINGS, LocationType.VILLAGE
    ),
    THARBAD("thb",
        { listOf(CARDOLAN, MINHIRIATH, ENEDWAITH, SOUTH_DUNLAND, NORTH_DUNLAND) }),
    THE_SHIRE(
        "sh",
        { listOf(TOWER_HILLS, SOUTH_ERED_LUIN, OLD_FOREST, BUCKLAND, EVENDIM) }, NationName.NORTHMEN, LocationType.CITY
    ),
    TOWER_HILLS("th",
        { listOf(GREY_HAVENS, SOUTH_ERED_LUIN, THE_SHIRE, EVENDIM, ERED_LUIN) }),
    TROLLSHAWS("tr",
        { listOf(ETTENMOORS, WEATHER_HILLS, SOUTH_DOWNS, HOLLIN, FORDS_OF_BRUINEN, RIVENDELL) }),
    UMBAR(
        "um",
        { listOf(WEST_HARONDOR, NEAR_HARAD) }, NationName.SOUTHRONS_AND_EASTERLINGS, LocationType.STRONGHOLD
    ),
    VALE_OF_THE_CARNEN("vca",
        { listOf(IRON_HILLS, DALE, NORTHERN_RHOVANION, VALE_OF_THE_CELDUIN, NORTH_RHUN, EAST_RHUN) }),
    VALE_OF_THE_CELDUIN("vce",
        { listOf(VALE_OF_THE_CARNEN, NORTHERN_RHOVANION, SOUTHERN_RHOVANION, NORTHERN_DORWINION, NORTH_RHUN) }),
    WEATHER_HILLS("whi",
        { listOf(ETTENMOORS, NORTH_DOWNS, BREE, SOUTH_DOWNS, TROLLSHAWS) }),
    WESTEMNET(
        "we",
        { listOf(FANGORN, FORDS_OF_ISEN, HELMS_DEEP, EDORAS, FOLDE, EASTEMNET) }, NationName.ROHAN, LocationType.VILLAGE
    ),
    WESTERN_BROWN_LANDS("wbl",
        { listOf(DOL_GULDUR, SOUTH_ANDUIN_VALE, PARTH_CELEBRANT, EASTEMNET, WESTERN_EMYN_MUIL, EASTERN_BROWN_LANDS) }),
    WESTERN_EMYN_MUIL("wem",
        { listOf(WESTERN_BROWN_LANDS, EASTEMNET, DRUADAN_FOREST, DEAD_MARSHES, EASTERN_EMYN_MUIL, EASTERN_BROWN_LANDS) }),
    WESTERN_MIRKWOOD("wm",
        { listOf(NORTHERN_MIRKWOOD, CARROCK, OLD_FOREST_ROAD, WOODLAND_REALM) }),
    WEST_HARONDOR("wha",
        { listOf(PELARGIR, UMBAR, NEAR_HARAD, EAST_HARONDOR, SOUTH_ITHILIEN, OSGILIATH) }),
    WITHERED_HEATH("wh",
        { listOf(NORTHERN_MIRKWOOD, WOODLAND_REALM, DALE, EREBOR) }),
    WOODLAND_REALM(
        "wr",
        { listOf(NORTHERN_MIRKWOOD, WESTERN_MIRKWOOD, OLD_FOREST_ROAD, DALE, WITHERED_HEATH) }, NationName.ELVES, LocationType.STRONGHOLD
    );

    val fullName = name.lowercase().replace("_", " ").replaceFirstChar { it.uppercaseChar() }

    fun isForeign(nation: NationName) = nation != this.nation

    override fun toString() = fullName

    companion object {
        fun search(prefix: String) = entries.flatMap { listOf(it.fullName, it.shortcut) }
            .filter { it.startsWith(prefix, ignoreCase = true) }
            .map { it.lowercase() }

        fun get(name: String) = entries.filter { it.fullName.equals(name, true) || it.shortcut.equals(name, true) }
            .let {
                if (it.isEmpty()) throw IllegalArgumentException("Invalid location $name")
                else if (it.size > 1) throw IllegalArgumentException("Multiple matching locations for $name: ${it.joinToString(", ")}")
                else it[0]
            }
    }
}
