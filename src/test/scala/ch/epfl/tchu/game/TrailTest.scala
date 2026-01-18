package ch.epfl.tchu.game

import org.scalatest.funsuite.AnyFunSuite

class TrailTest extends AnyFunSuite:

  test("trail longest works on given example"):
    val s1 = Station(1, "Yverdon")
    val s2 = Station(2, "Fribourg")
    val s3 = Station(3, "Neuchâtel")
    val s4 = Station(4, "Berne")
    val s5 = Station(5, "Lucerne")
    val s6 = Station(6, "Soleure")

    val routes = List(
      Route("A", s3, s1, 2, Level.Overground, Some(Color.Black)),
      Route("B", s3, s6, 4, Level.Overground, Some(Color.Green)),
      Route("C", s4, s3, 2, Level.Overground, Some(Color.Red)),
      Route("D", s4, s6, 2, Level.Overground, Some(Color.Black)),
      Route("E", s4, s5, 4, Level.Overground, None),
      Route("F", s4, s2, 1, Level.Overground, Some(Color.Orange))
    )

    val longest = Trail.longest(routes)
    assert(13 == longest.length)
    if longest.station1.contains(s2) then
      assert(longest.station2.contains(s5))
    else if longest.station2.contains(s2) then
      assert(longest.station1.contains(s5))
    else
      fail(s"Unexpected start station: ${longest.station1}")

  test("trail longest works with empty routes"):
    val longest = Trail.longest(List.empty)
    assert(0 == longest.length)
    assert(longest.station1.isEmpty)
    assert(longest.station2.isEmpty)

  test("trail longest works with disconnected routes"):
    val chRoutes = ChRoutes()
    val routes = List(
      chRoutes.SAR_VAD_1,
      chRoutes.BER_LUC_1,
      chRoutes.GEN_YVE_1,
      chRoutes.IT3_LUG_1
    )
    val longest = Trail.longest(routes)
    assert(6 == longest.length)
    if longest.station1.contains(chRoutes.GEN) then
      assert(longest.station2.contains(chRoutes.YVE))
    else if longest.station1.contains(chRoutes.YVE) then
      assert(longest.station2.contains(chRoutes.GEN))
    else
      fail(s"Unexpected start station: ${longest.station1}")

  test("trail longest works with single cycle"):
    val chRoutes = ChRoutes()
    val routes = List(
      chRoutes.FRI_LAU_1,
      chRoutes.BER_FRI_1,
      chRoutes.BER_LUC_1,
      chRoutes.INT_LUC_1,
      chRoutes.BRI_INT_1,
      chRoutes.BRI_SIO_1,
      chRoutes.MAR_SIO_1,
      chRoutes.LAU_MAR_1
    )
    val longest = Trail.longest(routes)
    assert(23 == longest.length)

  test("trail longest works with double cycle"):
    val chRoutes = ChRoutes()
    val routes = List(
      chRoutes.BER_SOL_1,
      chRoutes.OLT_SOL_1,
      chRoutes.LUC_OLT_1,
      chRoutes.INT_LUC_1,
      chRoutes.BER_INT_1,
      chRoutes.BER_LUC_1
    )
    val longest = Trail.longest(routes)
    assert(17 == longest.length)

  private class ChRoutes:
    // Stations
    val BAD = Station(0, "Baden")
    val BAL = Station(1, "Bâle")
    val BEL = Station(2, "Bellinzone")
    val BER = Station(3, "Berne")
    val BRI = Station(4, "Brigue")
    val BRU = Station(5, "Brusio")
    val COI = Station(6, "Coire")
    val DAV = Station(7, "Davos")
    val DEL = Station(8, "Delémont")
    val FRI = Station(9, "Fribourg")
    val GEN = Station(10, "Genève")
    val INT = Station(11, "Interlaken")
    val KRE = Station(12, "Kreuzlingen")
    val LAU = Station(13, "Lausanne")
    val LCF = Station(14, "La Chaux-de-Fonds")
    val LOC = Station(15, "Locarno")
    val LUC = Station(16, "Lucerne")
    val LUG = Station(17, "Lugano")
    val MAR = Station(18, "Martigny")
    val NEU = Station(19, "Neuchâtel")
    val OLT = Station(20, "Olten")
    val PFA = Station(21, "Pfäffikon")
    val SAR = Station(22, "Sargans")
    val SCE = Station(23, "Schaffhouse")
    val SCZ = Station(24, "Schwyz")
    val SIO = Station(25, "Sion")
    val SOL = Station(26, "Soleure")
    val STG = Station(27, "Saint-Gall")
    val VAD = Station(28, "Vaduz")
    val WAS = Station(29, "Wassen")
    val WIN = Station(30, "Winterthour")
    val YVE = Station(31, "Yverdon")
    val ZOU = Station(32, "Zoug")
    val ZUR = Station(33, "Zürich")
    val IT3 = Station(44, "Italie")

    // Routes
    val BER_FRI_1 = Route("BER_FRI_1", BER, FRI, 1, Level.Overground, Some(Color.Orange))
    val BER_INT_1 = Route("BER_INT_1", BER, INT, 3, Level.Overground, Some(Color.Blue))
    val BER_LUC_1 = Route("BER_LUC_1", BER, LUC, 4, Level.Overground, None)
    val BER_SOL_1 = Route("BER_SOL_1", BER, SOL, 2, Level.Overground, Some(Color.Black))
    val BRI_INT_1 = Route("BRI_INT_1", BRI, INT, 2, Level.Underground, Some(Color.White))
    val BRI_SIO_1 = Route("BRI_SIO_1", BRI, SIO, 3, Level.Underground, Some(Color.Black))
    val FRI_LAU_1 = Route("FRI_LAU_1", FRI, LAU, 3, Level.Overground, Some(Color.Red))
    val GEN_YVE_1 = Route("GEN_YVE_1", GEN, YVE, 6, Level.Overground, None)
    val INT_LUC_1 = Route("INT_LUC_1", INT, LUC, 4, Level.Overground, Some(Color.Violet))
    val IT3_LUG_1 = Route("IT3_LUG_1", IT3, LUG, 2, Level.Underground, Some(Color.White))
    val LAU_MAR_1 = Route("LAU_MAR_1", LAU, MAR, 4, Level.Underground, Some(Color.Orange))
    val LUC_OLT_1 = Route("LUC_OLT_1", LUC, OLT, 3, Level.Overground, Some(Color.Green))
    val MAR_SIO_1 = Route("MAR_SIO_1", MAR, SIO, 2, Level.Underground, Some(Color.Green))
    val OLT_SOL_1 = Route("OLT_SOL_1", OLT, SOL, 1, Level.Overground, Some(Color.Blue))
    val SAR_VAD_1 = Route("SAR_VAD_1", SAR, VAD, 1, Level.Underground, Some(Color.Orange))
