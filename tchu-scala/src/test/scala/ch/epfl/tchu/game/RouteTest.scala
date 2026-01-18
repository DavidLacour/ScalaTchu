package ch.epfl.tchu.game

import ch.epfl.tchu.SortedBag
import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite

class RouteTest extends AnyFunSuite:

  private val COLORS = List(
    Color.Black, Color.Violet, Color.Blue, Color.Green,
    Color.Yellow, Color.Orange, Color.Red, Color.White
  )
  private val CAR_CARDS = List(
    Card.Black, Card.Violet, Card.Blue, Card.Green,
    Card.Yellow, Card.Orange, Card.Red, Card.White
  )

  test("route constructor fails when both stations are equal"):
    val s = Station(0, "Lausanne")
    assertThrows[IllegalArgumentException]:
      Route("id", s, s, 1, Level.Overground, Some(Color.Black))

  test("route constructor fails when length is invalid"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    assertThrows[IllegalArgumentException]:
      Route("id", s1, s2, 0, Level.Overground, Some(Color.Black))
    assertThrows[IllegalArgumentException]:
      Route("id", s1, s2, 7, Level.Overground, Some(Color.Black))

  test("route id returns id"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val routes = (0 until 100).map(i => Route(s"id$i", s1, s2, 1, Level.Overground, Some(Color.Black)))
    for i <- 0 until 100 do
      assert(s"id$i" == routes(i).id)

  test("route station1 and station2 return station1 and station2"):
    val rng = TestRandomizer.newRandom()
    val stations = (0 until 100).map(i => Station(i, s"Station $i"))
    val routes = (0 until 100).map: i =>
      val s1 = stations(i)
      val s2 = stations((i + 1) % 100)
      val l = 1 + rng.nextInt(6)
      Route(s"r$i", s1, s2, l, Level.Overground, Some(Color.Red))

    for i <- 0 until 100 do
      val s1 = stations(i)
      val s2 = stations((i + 1) % 100)
      val r = routes(i)
      assert(s1 == r.station1)
      assert(s2 == r.station2)

  test("route length returns length"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"
    val routes = (1 to 6).map(l => Route(id, s1, s2, l, Level.Overground, Some(Color.Black)))
    for l <- 1 to 6 do
      assert(l == routes(l - 1).length)

  test("route level returns level"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"
    val ro = Route(id, s1, s2, 1, Level.Overground, Some(Color.Black))
    val ru = Route(id, s1, s2, 1, Level.Underground, Some(Color.Black))
    assert(Level.Overground == ro.level)
    assert(Level.Underground == ru.level)

  test("route color returns color"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"
    val routes = COLORS.map(c => Route(id, s1, s2, 1, Level.Overground, Some(c)))
    for c <- COLORS do
      assert(Some(c) == routes(c.ordinal).color)
    val r = Route(id, s1, s2, 1, Level.Overground, None)
    assert(r.color.isEmpty)

  test("route stations returns stations"):
    val rng = TestRandomizer.newRandom()
    val stations = (0 until 100).map(i => Station(i, s"Station $i"))
    val routes = (0 until 100).map: i =>
      val s1 = stations(i)
      val s2 = stations((i + 1) % 100)
      val l = 1 + rng.nextInt(6)
      Route(s"r$i", s1, s2, l, Level.Overground, Some(Color.Red))

    for i <- 0 until 100 do
      val s1 = stations(i)
      val s2 = stations((i + 1) % 100)
      assert(List(s1, s2) == routes(i).stations)

  test("route stationOpposite fails with invalid station"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val s3 = Station(2, "Geneva") // Different station not on the route
    val r = Route("id", s1, s2, 1, Level.Overground, Some(Color.Red))
    assertThrows[IllegalArgumentException]:
      r.stationOpposite(s3)

  test("route stationOpposite returns opposite station"):
    val rng = TestRandomizer.newRandom()
    val stations = (0 until 100).map(i => Station(i, s"Station $i"))
    val routes = (0 until 100).map: i =>
      val s1 = stations(i)
      val s2 = stations((i + 1) % 100)
      val l = 1 + rng.nextInt(6)
      Route(s"r$i", s1, s2, l, Level.Overground, Some(Color.Red))

    for i <- 0 until 100 do
      val s1 = stations(i)
      val s2 = stations((i + 1) % 100)
      val r = routes(i)
      assert(s1 == r.stationOpposite(s2))
      assert(s2 == r.stationOpposite(s1))

  test("route possibleClaimCards works for overground colored route"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"
    for i <- COLORS.indices do
      val color = COLORS(i)
      val card = CAR_CARDS(i)
      for l <- 1 to 6 do
        val r = Route(id, s1, s2, l, Level.Overground, Some(color))
        assert(List(SortedBag.of(l, card)) == r.possibleClaimCards)

  test("route possibleClaimCards works on overground neutral route"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"
    for l <- 1 to 6 do
      val r = Route(id, s1, s2, l, Level.Overground, None)
      val expected = List(
        SortedBag.of(l, Card.Black),
        SortedBag.of(l, Card.Violet),
        SortedBag.of(l, Card.Blue),
        SortedBag.of(l, Card.Green),
        SortedBag.of(l, Card.Yellow),
        SortedBag.of(l, Card.Orange),
        SortedBag.of(l, Card.Red),
        SortedBag.of(l, Card.White)
      )
      assert(expected == r.possibleClaimCards)

  test("route possibleClaimCards works on underground colored route"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"
    for i <- COLORS.indices do
      val color = COLORS(i)
      val card = CAR_CARDS(i)
      for l <- 1 to 6 do
        val r = Route(id, s1, s2, l, Level.Underground, Some(color))
        val expected = (0 to l).map: locomotives =>
          val cars = l - locomotives
          SortedBag.of(cars, card, locomotives, Card.Locomotive)
        .toList
        assert(expected == r.possibleClaimCards)

  test("route possibleClaimCards works on underground neutral route"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"
    for l <- 1 to 6 do
      val r = Route(id, s1, s2, l, Level.Underground, None)
      var expected = List.empty[SortedBag[Card]]
      for locomotives <- 0 to l do
        val cars = l - locomotives
        if cars == 0 then
          expected = expected :+ SortedBag.of(locomotives, Card.Locomotive)
        else
          for card <- CAR_CARDS do
            expected = expected :+ SortedBag.of(cars, card, locomotives, Card.Locomotive)
      assert(expected == r.possibleClaimCards)

  test("route additionalClaimCardsCount works with colored cards only"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"

    for l <- 1 to 6 do
      for color <- COLORS do
        val matchingCard = CAR_CARDS(color.ordinal)
        val nonMatchingCard = if color == Color.Black then Card.White else Card.Black
        val claimCards = SortedBag.of(l, matchingCard)
        val r = Route(id, s1, s2, l, Level.Underground, Some(color))
        for m <- 0 to 3 do
          for locomotives <- 0 to m do
            val drawnBuilder = SortedBag.Builder[Card]()
            drawnBuilder.add(locomotives, Card.Locomotive)
            drawnBuilder.add(m - locomotives, matchingCard)
            drawnBuilder.add(3 - m, nonMatchingCard)
            val drawn = drawnBuilder.build()
            assert(m == r.additionalClaimCardsCount(claimCards, drawn))

  test("route additionalClaimCardsCount works with locomotives only"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"

    for l <- 1 to 6 do
      for color <- COLORS do
        val matchingCard = CAR_CARDS(color.ordinal)
        val nonMatchingCard = if color == Color.Black then Card.White else Card.Black
        val claimCards = SortedBag.of(l, Card.Locomotive)
        val r = Route(id, s1, s2, l, Level.Underground, Some(color))
        for m <- 0 to 3 do
          for locomotives <- 0 to m do
            val drawnBuilder = SortedBag.Builder[Card]()
            drawnBuilder.add(locomotives, Card.Locomotive)
            drawnBuilder.add(m - locomotives, matchingCard)
            drawnBuilder.add(3 - m, nonMatchingCard)
            val drawn = drawnBuilder.build()
            assert(locomotives == r.additionalClaimCardsCount(claimCards, drawn))

  test("route claimPoints returns claim points"):
    val expectedClaimPoints = List(Int.MinValue, 1, 2, 4, 7, 10, 15)
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"
    for l <- 1 to 6 do
      val r = Route(id, s1, s2, l, Level.Overground, Some(Color.Black))
      assert(expectedClaimPoints(l) == r.claimPoints)
