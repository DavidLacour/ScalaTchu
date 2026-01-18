package ch.epfl.tchu.game

import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random

class PublicPlayerStateTest extends AnyFunSuite:

  private val TOTAL_CAR_COUNT = 40

  test("publicPlayerState constructor fails with negative ticket or card count"):
    assertThrows[IllegalArgumentException]:
      PublicPlayerState(-1, +1, List.empty)
    assertThrows[IllegalArgumentException]:
      PublicPlayerState(+1, -1, List.empty)

  test("publicPlayerState ticketCount returns ticket count"):
    for t <- 0 until 10 do
      val state = PublicPlayerState(t, 0, List.empty)
      assert(t == state.ticketCount)

  test("publicPlayerState cardCount returns card count"):
    for c <- 0 until 10 do
      val state = PublicPlayerState(0, c, List.empty)
      assert(c == state.cardCount)

  test("publicPlayerState routes returns routes"):
    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val routes = randomRoutes(rng)
      val state = PublicPlayerState(0, 0, routes)
      assert(routes == state.routes)

  test("publicPlayerState carCount works"):
    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val routes = randomRoutes(rng)
      val routesLength = routes.map(_.length).sum

      val state = PublicPlayerState(0, 0, routes)
      assert(TOTAL_CAR_COUNT - routesLength == state.carCount)

  test("publicPlayerState claimPoints works"):
    val rng = TestRandomizer.newRandom()
    val claimPoints = List(Int.MinValue, 1, 2, 4, 7, 10, 15)
    for _ <- 0 until TestRandomizer.RandomIterations do
      val routes = randomRoutes(rng)
      val points = routes.map(r => claimPoints(r.length)).sum

      val state = PublicPlayerState(0, 0, routes)
      assert(points == state.claimPoints)

  private def randomRoutes(rng: Random): List[Route] =
    val shuffledRoutes = Random.shuffle(ChRoutes().ALL_ROUTES)

    val maxRoutesCount = rng.nextInt(TOTAL_CAR_COUNT)
    var routes = List.empty[Route]
    var routesLength = 0
    for route <- shuffledRoutes.take(maxRoutesCount) do
      val totalLength = routesLength + route.length
      if totalLength <= TOTAL_CAR_COUNT then
        routes = routes :+ route
        routesLength = totalLength
    routes

  private class ChRoutes:
    // Stations
    val BAD = Station(0, "Baden")
    val BAL = Station(1, "Bâle")
    val BEL = Station(2, "Bellinzone")
    val BER = Station(3, "Berne")
    val BRI = Station(4, "Brigue")
    val COI = Station(6, "Coire")
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

    // Routes
    val BER_FRI_1 = Route("BER_FRI_1", BER, FRI, 1, Level.Overground, Some(Color.Orange))
    val BER_INT_1 = Route("BER_INT_1", BER, INT, 3, Level.Overground, Some(Color.Blue))
    val BER_LUC_1 = Route("BER_LUC_1", BER, LUC, 4, Level.Overground, None)
    val BER_NEU_1 = Route("BER_NEU_1", BER, NEU, 2, Level.Overground, Some(Color.Red))
    val BER_SOL_1 = Route("BER_SOL_1", BER, SOL, 2, Level.Overground, Some(Color.Black))
    val BRI_LOC_1 = Route("BRI_LOC_1", BRI, LOC, 6, Level.Underground, None)
    val FRI_LAU_1 = Route("FRI_LAU_1", FRI, LAU, 3, Level.Overground, Some(Color.Red))
    val GEN_LAU_1 = Route("GEN_LAU_1", GEN, LAU, 4, Level.Overground, Some(Color.Blue))
    val GEN_YVE_1 = Route("GEN_YVE_1", GEN, YVE, 6, Level.Overground, None)
    val INT_LUC_1 = Route("INT_LUC_1", INT, LUC, 4, Level.Overground, Some(Color.Violet))
    val LUC_OLT_1 = Route("LUC_OLT_1", LUC, OLT, 3, Level.Overground, Some(Color.Green))
    val LUC_ZOU_1 = Route("LUC_ZOU_1", LUC, ZOU, 1, Level.Overground, Some(Color.Orange))
    val OLT_SOL_1 = Route("OLT_SOL_1", OLT, SOL, 1, Level.Overground, Some(Color.Blue))
    val OLT_ZUR_1 = Route("OLT_ZUR_1", OLT, ZUR, 3, Level.Overground, Some(Color.White))
    val SCE_WIN_1 = Route("SCE_WIN_1", SCE, WIN, 1, Level.Overground, Some(Color.Black))
    val STG_ZUR_1 = Route("STG_ZUR_1", STG, ZUR, 4, Level.Overground, Some(Color.Black))
    val WIN_ZUR_1 = Route("WIN_ZUR_1", WIN, ZUR, 1, Level.Overground, Some(Color.Blue))
    val ZOU_ZUR_1 = Route("ZOU_ZUR_1", ZOU, ZUR, 1, Level.Overground, Some(Color.Green))

    val ALL_ROUTES = List(
      BER_FRI_1, BER_INT_1, BER_LUC_1, BER_NEU_1, BER_SOL_1,
      BRI_LOC_1, FRI_LAU_1, GEN_LAU_1, GEN_YVE_1, INT_LUC_1,
      LUC_OLT_1, LUC_ZOU_1, OLT_SOL_1, OLT_ZUR_1, SCE_WIN_1,
      STG_ZUR_1, WIN_ZUR_1, ZOU_ZUR_1
    )
