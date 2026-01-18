package ch.epfl.tchu.game

import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random

class StationPartitionTest extends AnyFunSuite:

  test("station partition initially connects stations with themselves only"):
    val stations = ChMap().ALL_STATIONS

    val partition = StationPartition.Builder(stations.size).build()
    for s1 <- stations do
      for s2 <- stations do
        val same = s1 == s2
        assert(same == partition.connected(s1, s2))

  test("station partition builder connect is idempotent"):
    val stations = ChMap().ALL_STATIONS
    val s0 = stations.head
    val s1 = stations(1)
    val partition = StationPartition.Builder(stations.size)
      .connect(s0, s0)
      .connect(s1, s1)
      .connect(s0, s1)
      .connect(s1, s0)
      .build()

    assert(partition.connected(s0, s0))
    assert(partition.connected(s1, s1))
    assert(partition.connected(s0, s1))
    assert(partition.connected(s1, s0))

  test("station partition works on given example"):
    val stations = reducedChStations()
    val partition = StationPartition.Builder(stations.size)
      .connect(stations(5), stations(2))  // Lausanne - Fribourg
      .connect(stations(0), stations(3))  // Berne - Interlaken
      .connect(stations(0), stations(2))  // Berne - Fribourg
      .connect(stations(7), stations(10)) // Neuchâtel - Soleure
      .connect(stations(10), stations(8)) // Soleure - Olten
      .connect(stations(6), stations(13)) // Lucerne - Zoug
      .connect(stations(13), stations(9)) // Zoug - Schwyz
      .connect(stations(9), stations(6))  // Schwyz - Lucerne
      .connect(stations(9), stations(11)) // Schwyz - Wassen
      .build()

    assert(partition.connected(stations(5), stations(3)))   // Lausanne - Interlaken
    assert(partition.connected(stations(6), stations(11)))  // Lucerne - Wassen
    assert(partition.connected(stations(13), stations(11))) // Zoug - Wassen
    assert(partition.connected(stations(9), stations(11)))  // Schwyz - Wassen

    assert(!partition.connected(stations(0), stations(6)))  // Berne - Lucerne

  test("station partition works on known example 1"):
    val chMap = ChMap()

    val routes = List(
      chMap.BRI_LOC_1, chMap.BRI_SIO_1, chMap.MAR_SIO_1, chMap.LAU_MAR_1,
      chMap.GEN_LAU_1, chMap.GEN_YVE_1, chMap.LCF_YVE_1, chMap.DEL_LCF_1,
      chMap.DEL_SOL_1, chMap.OLT_SOL_1, chMap.BAL_OLT_1, chMap.BER_LUC_1,
      chMap.SCE_WIN_1
    )
    val maxId = routes.flatMap(_.stations).map(_.id).max

    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val shuffledRoutes = Random.shuffle(routes)
      val builder = StationPartition.Builder(maxId + 1)
      shuffledRoutes.foreach(r => builder.connect(r.station1, r.station2))
      val p = builder.build()

      assert(p.connected(chMap.LOC, chMap.BAL))
      assert(p.connected(chMap.BER, chMap.LUC))
      assert(!p.connected(chMap.BER, chMap.SOL))
      assert(!p.connected(chMap.LAU, chMap.LUC))
      assert(!p.connected(chMap.ZUR, chMap.KRE))
      assert(p.connected(chMap.ZUR, chMap.ZUR))

  private def reducedChStations(): List[Station] =
    List(
      Station(0, "Berne"),
      Station(1, "Delémont"),
      Station(2, "Fribourg"),
      Station(3, "Interlaken"),
      Station(4, "La Chaux-de-Fonds"),
      Station(5, "Lausanne"),
      Station(6, "Lucerne"),
      Station(7, "Neuchâtel"),
      Station(8, "Olten"),
      Station(9, "Schwyz"),
      Station(10, "Soleure"),
      Station(11, "Wassen"),
      Station(12, "Yverdon"),
      Station(13, "Zoug"),
      Station(14, "Zürich")
    )

  private class ChMap:
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

    val ALL_STATIONS = List(
      BAD, BAL, BEL, BER, BRI, BRU, COI, DAV, DEL, FRI, GEN, INT, KRE, LAU, LCF, LOC, LUC,
      LUG, MAR, NEU, OLT, PFA, SAR, SCE, SCZ, SIO, SOL, STG, VAD, WAS, WIN, YVE, ZOU, ZUR
    )

    // Routes
    val BAL_OLT_1 = Route("BAL_OLT_1", BAL, OLT, 2, Level.Underground, Some(Color.Orange))
    val BER_LUC_1 = Route("BER_LUC_1", BER, LUC, 4, Level.Overground, None)
    val BRI_LOC_1 = Route("BRI_LOC_1", BRI, LOC, 6, Level.Underground, None)
    val BRI_SIO_1 = Route("BRI_SIO_1", BRI, SIO, 3, Level.Underground, Some(Color.Black))
    val DEL_LCF_1 = Route("DEL_LCF_1", DEL, LCF, 3, Level.Underground, Some(Color.White))
    val DEL_SOL_1 = Route("DEL_SOL_1", DEL, SOL, 1, Level.Underground, Some(Color.Violet))
    val GEN_LAU_1 = Route("GEN_LAU_1", GEN, LAU, 4, Level.Overground, Some(Color.Blue))
    val GEN_YVE_1 = Route("GEN_YVE_1", GEN, YVE, 6, Level.Overground, None)
    val LAU_MAR_1 = Route("LAU_MAR_1", LAU, MAR, 4, Level.Underground, Some(Color.Orange))
    val LCF_YVE_1 = Route("LCF_YVE_1", LCF, YVE, 3, Level.Underground, Some(Color.Yellow))
    val MAR_SIO_1 = Route("MAR_SIO_1", MAR, SIO, 2, Level.Underground, Some(Color.Green))
    val OLT_SOL_1 = Route("OLT_SOL_1", OLT, SOL, 1, Level.Overground, Some(Color.Blue))
    val SCE_WIN_1 = Route("SCE_WIN_1", SCE, WIN, 1, Level.Overground, Some(Color.Black))
