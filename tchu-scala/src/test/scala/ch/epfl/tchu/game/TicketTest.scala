package ch.epfl.tchu.game

import org.scalatest.funsuite.AnyFunSuite

class TicketTest extends AnyFunSuite:

  test("constructor fails with no trips"):
    assertThrows[IllegalArgumentException]:
      Ticket(List.empty)

  test("text is correct for simple ticket"):
    val s1 = Station(0, "From")
    val s2 = Station(1, "To")
    val t = Ticket(s1, s2, 1)
    assert("From - To (1)" == t.text)

  test("text is correct for single trip ticket"):
    val s1 = Station(0, "From")
    val s2 = Station(1, "To")
    val t = Ticket(List(Trip(s1, s2, 15)))
    assert("From - To (15)" == t.text)

  test("text is correct for multiple trip ticket"):
    val map = TestMap()
    assert(
      "Berne - {Allemagne (6), Autriche (11), France (5), Italie (8)}" == map.BER_NEIGHBORS.text
    )
    assert(
      "France - {Allemagne (5), Autriche (14), Italie (11)}" == map.FR_NEIGHBORS.text
    )

  test("points are correct with no connectivity"):
    val map = TestMap()
    val connectivity = TestConnectivity(List.empty, List.empty)
    assert(-13 == map.LAU_STG.points(connectivity))
    assert(-5 == map.BER_NEIGHBORS.points(connectivity))
    assert(-5 == map.FR_NEIGHBORS.points(connectivity))

  test("points are correct with full connectivity"):
    val map = TestMap()
    val connectivity = FullConnectivity()
    assert(+13 == map.LAU_STG.points(connectivity))
    assert(+11 == map.BER_NEIGHBORS.points(connectivity))
    assert(+14 == map.FR_NEIGHBORS.points(connectivity))

  test("points are correct with partial connectivity"):
    val map = TestMap()
    val connectivity = TestConnectivity(
      List(map.LAU, map.BER, map.BER, map.FR1, map.FR2, map.FR2),
      List(map.BER, map.FR2, map.DE3, map.IT1, map.IT2, map.DE1)
    )
    assert(-13 == map.LAU_STG.points(connectivity))
    assert(+6 == map.BER_NEIGHBORS.points(connectivity))
    assert(+11 == map.FR_NEIGHBORS.points(connectivity))

  test("compareTo works on known tickets"):
    val map = TestMap()
    assert(map.LAU_BER.compare(map.LAU_STG) < 0)
    assert(map.LAU_BER.compare(map.FR_NEIGHBORS) > 0)
    assert(0 == map.LAU_BER.compare(map.LAU_BER))

  private class FullConnectivity extends StationConnectivity:
    def connected(s1: Station, s2: Station): Boolean = true

  private class TestConnectivity(stations1: List[Station], stations2: List[Station]) extends StationConnectivity:
    require(stations1.size == stations2.size)

    def connected(s1: Station, s2: Station): Boolean =
      stations1.zip(stations2).exists:
        case (t1, t2) => (t1 == s1 && t2 == s2) || (t1 == s2 && t2 == s1)

  private class TestMap:
    // Stations - cities
    val BER = Station(0, "Berne")
    val LAU = Station(1, "Lausanne")
    val STG = Station(2, "Saint-Gall")

    // Stations - countries
    val DE1 = Station(3, "Allemagne")
    val DE2 = Station(4, "Allemagne")
    val DE3 = Station(5, "Allemagne")
    val AT1 = Station(6, "Autriche")
    val AT2 = Station(7, "Autriche")
    val IT1 = Station(8, "Italie")
    val IT2 = Station(9, "Italie")
    val IT3 = Station(10, "Italie")
    val FR1 = Station(11, "France")
    val FR2 = Station(12, "France")

    // Countries
    val DE = List(DE1, DE2, DE3)
    val AT = List(AT1, AT2)
    val IT = List(IT1, IT2, IT3)
    val FR = List(FR1, FR2)

    val LAU_STG = Ticket(LAU, STG, 13)
    val LAU_BER = Ticket(LAU, BER, 2)
    val BER_NEIGHBORS = ticketToNeighbors(List(BER), 6, 11, 8, 5)
    val FR_NEIGHBORS = ticketToNeighbors(FR, 5, 14, 11, 0)

    private def ticketToNeighbors(from: List[Station], de: Int, at: Int, it: Int, fr: Int): Ticket =
      var trips = List.empty[Trip]
      if de != 0 then trips = trips ++ Trip.all(from, DE, de)
      if at != 0 then trips = trips ++ Trip.all(from, AT, at)
      if it != 0 then trips = trips ++ Trip.all(from, IT, it)
      if fr != 0 then trips = trips ++ Trip.all(from, FR, fr)
      Ticket(trips)
