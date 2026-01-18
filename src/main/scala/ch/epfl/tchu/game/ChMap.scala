package ch.epfl.tchu.game

/** Swiss map data: all stations, routes, and tickets for the game. */
object ChMap:

  def stations: List[Station] = AllStations
  def routes: List[Route] = AllRoutes
  def tickets: List[Ticket] = AllTickets

  // Stations - cities
  private val BAD = Station(0, "Baden")
  private val BAL = Station(1, "Bâle")
  private val BEL = Station(2, "Bellinzone")
  private val BER = Station(3, "Berne")
  private val BRI = Station(4, "Brigue")
  private val BRU = Station(5, "Brusio")
  private val COI = Station(6, "Coire")
  private val DAV = Station(7, "Davos")
  private val DEL = Station(8, "Delémont")
  private val FRI = Station(9, "Fribourg")
  private val GEN = Station(10, "Genève")
  private val INT = Station(11, "Interlaken")
  private val KRE = Station(12, "Kreuzlingen")
  private val LAU = Station(13, "Lausanne")
  private val LCF = Station(14, "La Chaux-de-Fonds")
  private val LOC = Station(15, "Locarno")
  private val LUC = Station(16, "Lucerne")
  private val LUG = Station(17, "Lugano")
  private val MAR = Station(18, "Martigny")
  private val NEU = Station(19, "Neuchâtel")
  private val OLT = Station(20, "Olten")
  private val PFA = Station(21, "Pfäffikon")
  private val SAR = Station(22, "Sargans")
  private val SCE = Station(23, "Schaffhouse")
  private val SCZ = Station(24, "Schwyz")
  private val SIO = Station(25, "Sion")
  private val SOL = Station(26, "Soleure")
  private val STG = Station(27, "Saint-Gall")
  private val VAD = Station(28, "Vaduz")
  private val WAS = Station(29, "Wassen")
  private val WIN = Station(30, "Winterthour")
  private val YVE = Station(31, "Yverdon")
  private val ZOU = Station(32, "Zoug")
  private val ZUR = Station(33, "Zürich")

  // Stations - countries
  private val DE1 = Station(34, "Allemagne")
  private val DE2 = Station(35, "Allemagne")
  private val DE3 = Station(36, "Allemagne")
  private val DE4 = Station(37, "Allemagne")
  private val DE5 = Station(38, "Allemagne")
  private val AT1 = Station(39, "Autriche")
  private val AT2 = Station(40, "Autriche")
  private val AT3 = Station(41, "Autriche")
  private val IT1 = Station(42, "Italie")
  private val IT2 = Station(43, "Italie")
  private val IT3 = Station(44, "Italie")
  private val IT4 = Station(45, "Italie")
  private val IT5 = Station(46, "Italie")
  private val FR1 = Station(47, "France")
  private val FR2 = Station(48, "France")
  private val FR3 = Station(49, "France")
  private val FR4 = Station(50, "France")

  // Countries
  private val DE = List(DE1, DE2, DE3, DE4, DE5)
  private val AT = List(AT1, AT2, AT3)
  private val IT = List(IT1, IT2, IT3, IT4, IT5)
  private val FR = List(FR1, FR2, FR3, FR4)

  private val AllStations = List(
    BAD, BAL, BEL, BER, BRI, BRU, COI, DAV, DEL, FRI, GEN, INT, KRE, LAU, LCF, LOC, LUC,
    LUG, MAR, NEU, OLT, PFA, SAR, SCE, SCZ, SIO, SOL, STG, VAD, WAS, WIN, YVE, ZOU, ZUR,
    DE1, DE2, DE3, DE4, DE5, AT1, AT2, AT3, IT1, IT2, IT3, IT4, IT5, FR1, FR2, FR3, FR4
  )

  // Routes
  private val AllRoutes = List(
    Route("AT1_STG_1", AT1, STG, 4, Level.Underground, None),
    Route("AT2_VAD_1", AT2, VAD, 1, Level.Underground, Some(Color.Red)),
    Route("BAD_BAL_1", BAD, BAL, 3, Level.Underground, Some(Color.Red)),
    Route("BAD_OLT_1", BAD, OLT, 2, Level.Overground, Some(Color.Violet)),
    Route("BAD_ZUR_1", BAD, ZUR, 1, Level.Overground, Some(Color.Yellow)),
    Route("BAL_DE1_1", BAL, DE1, 1, Level.Underground, Some(Color.Blue)),
    Route("BAL_DEL_1", BAL, DEL, 2, Level.Underground, Some(Color.Yellow)),
    Route("BAL_OLT_1", BAL, OLT, 2, Level.Underground, Some(Color.Orange)),
    Route("BEL_LOC_1", BEL, LOC, 1, Level.Underground, Some(Color.Black)),
    Route("BEL_LUG_1", BEL, LUG, 1, Level.Underground, Some(Color.Red)),
    Route("BEL_LUG_2", BEL, LUG, 1, Level.Underground, Some(Color.Yellow)),
    Route("BEL_WAS_1", BEL, WAS, 4, Level.Underground, None),
    Route("BEL_WAS_2", BEL, WAS, 4, Level.Underground, None),
    Route("BER_FRI_1", BER, FRI, 1, Level.Overground, Some(Color.Orange)),
    Route("BER_FRI_2", BER, FRI, 1, Level.Overground, Some(Color.Yellow)),
    Route("BER_INT_1", BER, INT, 3, Level.Overground, Some(Color.Blue)),
    Route("BER_LUC_1", BER, LUC, 4, Level.Overground, None),
    Route("BER_LUC_2", BER, LUC, 4, Level.Overground, None),
    Route("BER_NEU_1", BER, NEU, 2, Level.Overground, Some(Color.Red)),
    Route("BER_SOL_1", BER, SOL, 2, Level.Overground, Some(Color.Black)),
    Route("BRI_INT_1", BRI, INT, 2, Level.Underground, Some(Color.White)),
    Route("BRI_IT5_1", BRI, IT5, 3, Level.Underground, Some(Color.Green)),
    Route("BRI_LOC_1", BRI, LOC, 6, Level.Underground, None),
    Route("BRI_SIO_1", BRI, SIO, 3, Level.Underground, Some(Color.Black)),
    Route("BRI_WAS_1", BRI, WAS, 4, Level.Underground, Some(Color.Red)),
    Route("BRU_COI_1", BRU, COI, 5, Level.Underground, None),
    Route("BRU_DAV_1", BRU, DAV, 4, Level.Underground, Some(Color.Blue)),
    Route("BRU_IT2_1", BRU, IT2, 2, Level.Underground, Some(Color.Green)),
    Route("COI_DAV_1", COI, DAV, 2, Level.Underground, Some(Color.Violet)),
    Route("COI_SAR_1", COI, SAR, 1, Level.Underground, Some(Color.White)),
    Route("COI_WAS_1", COI, WAS, 5, Level.Underground, None),
    Route("DAV_AT3_1", DAV, AT3, 3, Level.Underground, None),
    Route("DAV_IT1_1", DAV, IT1, 3, Level.Underground, None),
    Route("DAV_SAR_1", DAV, SAR, 3, Level.Underground, Some(Color.Black)),
    Route("DE2_SCE_1", DE2, SCE, 1, Level.Overground, Some(Color.Yellow)),
    Route("DE3_KRE_1", DE3, KRE, 1, Level.Overground, Some(Color.Orange)),
    Route("DE4_KRE_1", DE4, KRE, 1, Level.Overground, Some(Color.White)),
    Route("DE5_STG_1", DE5, STG, 2, Level.Overground, None),
    Route("DEL_FR4_1", DEL, FR4, 2, Level.Underground, Some(Color.Black)),
    Route("DEL_LCF_1", DEL, LCF, 3, Level.Underground, Some(Color.White)),
    Route("DEL_SOL_1", DEL, SOL, 1, Level.Underground, Some(Color.Violet)),
    Route("FR1_MAR_1", FR1, MAR, 2, Level.Underground, None),
    Route("FR2_GEN_1", FR2, GEN, 1, Level.Overground, Some(Color.Yellow)),
    Route("FR3_LCF_1", FR3, LCF, 2, Level.Underground, Some(Color.Green)),
    Route("FRI_LAU_1", FRI, LAU, 3, Level.Overground, Some(Color.Red)),
    Route("FRI_LAU_2", FRI, LAU, 3, Level.Overground, Some(Color.Violet)),
    Route("GEN_LAU_1", GEN, LAU, 4, Level.Overground, Some(Color.Blue)),
    Route("GEN_LAU_2", GEN, LAU, 4, Level.Overground, Some(Color.White)),
    Route("GEN_YVE_1", GEN, YVE, 6, Level.Overground, None),
    Route("INT_LUC_1", INT, LUC, 4, Level.Overground, Some(Color.Violet)),
    Route("IT3_LUG_1", IT3, LUG, 2, Level.Underground, Some(Color.White)),
    Route("IT4_LOC_1", IT4, LOC, 2, Level.Underground, Some(Color.Orange)),
    Route("KRE_SCE_1", KRE, SCE, 3, Level.Overground, Some(Color.Violet)),
    Route("KRE_STG_1", KRE, STG, 1, Level.Overground, Some(Color.Green)),
    Route("KRE_WIN_1", KRE, WIN, 2, Level.Overground, Some(Color.Yellow)),
    Route("LAU_MAR_1", LAU, MAR, 4, Level.Underground, Some(Color.Orange)),
    Route("LAU_NEU_1", LAU, NEU, 4, Level.Overground, None),
    Route("LCF_NEU_1", LCF, NEU, 1, Level.Underground, Some(Color.Orange)),
    Route("LCF_YVE_1", LCF, YVE, 3, Level.Underground, Some(Color.Yellow)),
    Route("LOC_LUG_1", LOC, LUG, 1, Level.Underground, Some(Color.Violet)),
    Route("LUC_OLT_1", LUC, OLT, 3, Level.Overground, Some(Color.Green)),
    Route("LUC_SCZ_1", LUC, SCZ, 1, Level.Overground, Some(Color.Blue)),
    Route("LUC_ZOU_1", LUC, ZOU, 1, Level.Overground, Some(Color.Orange)),
    Route("LUC_ZOU_2", LUC, ZOU, 1, Level.Overground, Some(Color.Yellow)),
    Route("MAR_SIO_1", MAR, SIO, 2, Level.Underground, Some(Color.Green)),
    Route("NEU_SOL_1", NEU, SOL, 4, Level.Overground, Some(Color.Green)),
    Route("NEU_YVE_1", NEU, YVE, 2, Level.Overground, Some(Color.Black)),
    Route("OLT_SOL_1", OLT, SOL, 1, Level.Overground, Some(Color.Blue)),
    Route("OLT_ZUR_1", OLT, ZUR, 3, Level.Overground, Some(Color.White)),
    Route("PFA_SAR_1", PFA, SAR, 3, Level.Underground, Some(Color.Yellow)),
    Route("PFA_SCZ_1", PFA, SCZ, 1, Level.Overground, Some(Color.Violet)),
    Route("PFA_STG_1", PFA, STG, 3, Level.Overground, Some(Color.Orange)),
    Route("PFA_ZUR_1", PFA, ZUR, 2, Level.Overground, Some(Color.Blue)),
    Route("SAR_VAD_1", SAR, VAD, 1, Level.Underground, Some(Color.Orange)),
    Route("SCE_WIN_1", SCE, WIN, 1, Level.Overground, Some(Color.Black)),
    Route("SCE_WIN_2", SCE, WIN, 1, Level.Overground, Some(Color.White)),
    Route("SCE_ZUR_1", SCE, ZUR, 3, Level.Overground, Some(Color.Orange)),
    Route("SCZ_WAS_1", SCZ, WAS, 2, Level.Underground, Some(Color.Green)),
    Route("SCZ_WAS_2", SCZ, WAS, 2, Level.Underground, Some(Color.Yellow)),
    Route("SCZ_ZOU_1", SCZ, ZOU, 1, Level.Overground, Some(Color.Black)),
    Route("SCZ_ZOU_2", SCZ, ZOU, 1, Level.Overground, Some(Color.White)),
    Route("STG_VAD_1", STG, VAD, 2, Level.Underground, Some(Color.Blue)),
    Route("STG_WIN_1", STG, WIN, 3, Level.Overground, Some(Color.Red)),
    Route("STG_ZUR_1", STG, ZUR, 4, Level.Overground, Some(Color.Black)),
    Route("WIN_ZUR_1", WIN, ZUR, 1, Level.Overground, Some(Color.Blue)),
    Route("WIN_ZUR_2", WIN, ZUR, 1, Level.Overground, Some(Color.Violet)),
    Route("ZOU_ZUR_1", ZOU, ZUR, 1, Level.Overground, Some(Color.Green)),
    Route("ZOU_ZUR_2", ZOU, ZUR, 1, Level.Overground, Some(Color.Red))
  )

  // Tickets
  private def ticketToNeighbors(from: List[Station], de: Int, at: Int, it: Int, fr: Int): Ticket =
    var trips = List.empty[Trip]
    if de != 0 then trips = trips ++ Trip.all(from, DE, de)
    if at != 0 then trips = trips ++ Trip.all(from, AT, at)
    if it != 0 then trips = trips ++ Trip.all(from, IT, it)
    if fr != 0 then trips = trips ++ Trip.all(from, FR, fr)
    Ticket(trips)

  private val deToNeighbors = ticketToNeighbors(DE, 0, 5, 13, 5)
  private val atToNeighbors = ticketToNeighbors(AT, 5, 0, 6, 14)
  private val itToNeighbors = ticketToNeighbors(IT, 13, 6, 0, 11)
  private val frToNeighbors = ticketToNeighbors(FR, 5, 14, 11, 0)

  private val AllTickets = List(
    // City-to-city tickets
    Ticket(BAL, BER, 5),
    Ticket(BAL, BRI, 10),
    Ticket(BAL, STG, 8),
    Ticket(BER, COI, 10),
    Ticket(BER, LUG, 12),
    Ticket(BER, SCZ, 5),
    Ticket(BER, ZUR, 6),
    Ticket(FRI, LUC, 5),
    Ticket(GEN, BAL, 13),
    Ticket(GEN, BER, 8),
    Ticket(GEN, SIO, 10),
    Ticket(GEN, ZUR, 14),
    Ticket(INT, WIN, 7),
    Ticket(KRE, ZUR, 3),
    Ticket(LAU, INT, 7),
    Ticket(LAU, LUC, 8),
    Ticket(LAU, STG, 13),
    Ticket(LCF, BER, 3),
    Ticket(LCF, LUC, 7),
    Ticket(LCF, ZUR, 8),
    Ticket(LUC, VAD, 6),
    Ticket(LUC, ZUR, 2),
    Ticket(LUG, COI, 10),
    Ticket(NEU, WIN, 9),
    Ticket(OLT, SCE, 5),
    Ticket(SCE, MAR, 15),
    Ticket(SCE, STG, 4),
    Ticket(SCE, ZOU, 3),
    Ticket(STG, BRU, 9),
    Ticket(WIN, SCZ, 3),
    Ticket(ZUR, BAL, 4),
    Ticket(ZUR, BRU, 11),
    Ticket(ZUR, LUG, 9),
    Ticket(ZUR, VAD, 6),

    // City to country tickets
    ticketToNeighbors(List(BER), 6, 11, 8, 5),
    ticketToNeighbors(List(COI), 6, 3, 5, 12),
    ticketToNeighbors(List(LUG), 12, 13, 2, 14),
    ticketToNeighbors(List(ZUR), 3, 7, 11, 7),

    // Country to country tickets (two of each)
    deToNeighbors, deToNeighbors,
    atToNeighbors, atToNeighbors,
    itToNeighbors, itToNeighbors,
    frToNeighbors, frToNeighbors
  )
