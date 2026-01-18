package ch.epfl.tchu.game

import ch.epfl.tchu.SortedBag
import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random

class PlayerStateTest extends AnyFunSuite:

  private val INITIAL_CARD_COUNT = 4
  private val TOTAL_CAR_COUNT = 40

  private val COLORS = List(
    Color.Black, Color.Violet, Color.Blue, Color.Green,
    Color.Yellow, Color.Orange, Color.Red, Color.White
  )
  private val CAR_CARDS = List(
    Card.Black, Card.Violet, Card.Blue, Card.Green,
    Card.Yellow, Card.Orange, Card.Red, Card.White
  )

  test("playerState initial returns correct initial state"):
    val initialCards = SortedBag.of(INITIAL_CARD_COUNT, Card.Blue)
    val s = PlayerState.initial(initialCards)
    assert(SortedBag.of[Ticket] == s.tickets)
    assert(initialCards == s.cards)
    assert(List.empty == s.routes)
    assert(TOTAL_CAR_COUNT == s.carCount)
    assert(0 == s.claimPoints)
    assert(0 == s.ticketPoints)
    assert(0 == s.finalPoints)

  test("playerState constructor works"):
    val rng = TestRandomizer.newRandom()
    val chMap = ChMap()
    var routes = chMap.ALL_ROUTES
    var tickets = chMap.ALL_TICKETS
    var cards = shuffledCards(rng)
    for _ <- 0 until TestRandomizer.RandomIterations do
      routes = Random.shuffle(routes)
      tickets = Random.shuffle(tickets)
      cards = Random.shuffle(cards)

      val routesCount = rng.nextInt(7)
      val ticketsCount = rng.nextInt(tickets.size)
      val cardsCount = rng.nextInt(cards.size)

      val playerRoutes = routes.take(routesCount)
      val playerTickets = SortedBag.of(tickets.take(ticketsCount))
      val playerCards = SortedBag.of(cards.take(cardsCount))

      val playerState = PlayerState(playerTickets, playerCards, playerRoutes)

      assert(playerRoutes == playerState.routes)
      assert(playerTickets == playerState.tickets)
      assert(ticketsCount == playerState.ticketCount)
      assert(playerCards == playerState.cards)
      assert(cardsCount == playerState.cardCount)

  test("playerState withAddedTicket adds ticket"):
    val tickets = ChMap().ALL_TICKETS
    for batchSize <- 1 until 5 do
      var playerState = PlayerState(SortedBag.of[Ticket], SortedBag.of[Card], List.empty)
      var i = 0
      while i + batchSize < tickets.size do
        val nextI = i + batchSize
        val ticketsToAdd = tickets.slice(i, nextI)
        playerState = playerState.withAddedTickets(SortedBag.of(ticketsToAdd))
        assert(SortedBag.of(tickets.take(nextI)) == playerState.tickets)
        i = nextI

  test("playerState withAddedCard adds card"):
    val cards = shuffledCards(TestRandomizer.newRandom())
    var playerState = PlayerState(SortedBag.of[Ticket], SortedBag.of[Card], List.empty)
    for i <- cards.indices do
      val cardToAdd = cards(i)
      playerState = playerState.withAddedCard(cardToAdd)
      assert(SortedBag.of(cards.take(i + 1)) == playerState.cards)

  test("playerState withAddedCards adds cards"):
    val cards = shuffledCards(TestRandomizer.newRandom())
    for batchSize <- 1 until 5 do
      var playerState = PlayerState(SortedBag.of[Ticket], SortedBag.of[Card], List.empty)
      var i = 0
      while i + batchSize < cards.size do
        val nextI = i + batchSize
        val cardsToAdd = cards.slice(i, nextI)
        playerState = playerState.withAddedCards(SortedBag.of(cardsToAdd))
        assert(SortedBag.of(cards.take(nextI)) == playerState.cards)
        i = nextI

  test("playerState canClaimRoute works when not enough cars"):
    val chMap = ChMap()
    val cards = sixOfEachCard()
    for route <- chMap.ALL_ROUTES do
      for usedCars <- 30 to 40 do
        val routes = routesWithTotalLength(usedCars)
        val playerState = PlayerState(SortedBag.of[Ticket], cards, routes)
        val availableCars = TOTAL_CAR_COUNT - usedCars

        val claimable = availableCars >= route.length
        assert(claimable == playerState.canClaimRoute(route))

  test("playerState possibleClaimCards fails when not enough cars"):
    val chMap = ChMap()
    val cards = sixOfEachCard()
    for route <- chMap.ALL_ROUTES do
      for usedCars <- 30 to 40 do
        val routes = routesWithTotalLength(usedCars)
        val playerState = PlayerState(SortedBag.of[Ticket], cards, routes)
        val availableCars = TOTAL_CAR_COUNT - usedCars

        if availableCars < route.length then
          assertThrows[IllegalArgumentException]:
            playerState.possibleClaimCards(route)

  test("playerState possibleClaimCards works for overground colored route"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"

    val emptyPlayerState = PlayerState(SortedBag.of[Ticket], SortedBag.of[Card], List.empty)
    val fullPlayerState = PlayerState(SortedBag.of[Ticket], sixOfEachCard(), List.empty)

    for i <- COLORS.indices do
      val color = COLORS(i)
      val card = CAR_CARDS(i)
      for l <- 1 to 6 do
        val r = Route(id, s1, s2, l, Level.Overground, Some(color))

        assert(List.empty == emptyPlayerState.possibleClaimCards(r))
        assert(List(SortedBag.of(l, card)) == fullPlayerState.possibleClaimCards(r))

  test("playerState possibleClaimCards works on overground neutral route"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    val id = "id"

    val emptyPlayerState = PlayerState(SortedBag.of[Ticket], SortedBag.of[Card], List.empty)
    val kbyrlPlayerState = PlayerState(SortedBag.of[Ticket], sixOfKBYRL(), List.empty)
    val fullPlayerState = PlayerState(SortedBag.of[Ticket], sixOfEachCard(), List.empty)

    for l <- 1 to 6 do
      val r = Route(id, s1, s2, l, Level.Overground, None)

      assert(List.empty == emptyPlayerState.possibleClaimCards(r))

      val expectedKBYRL = List(
        SortedBag.of(l, Card.Black),
        SortedBag.of(l, Card.Blue),
        SortedBag.of(l, Card.Yellow),
        SortedBag.of(l, Card.Red)
      )
      assert(expectedKBYRL == kbyrlPlayerState.possibleClaimCards(r))

      val expectedFull = List(
        SortedBag.of(l, Card.Black),
        SortedBag.of(l, Card.Violet),
        SortedBag.of(l, Card.Blue),
        SortedBag.of(l, Card.Green),
        SortedBag.of(l, Card.Yellow),
        SortedBag.of(l, Card.Orange),
        SortedBag.of(l, Card.Red),
        SortedBag.of(l, Card.White)
      )
      assert(expectedFull == fullPlayerState.possibleClaimCards(r))

  test("playerState possibleAdditionalCards fails with invalid additional cards count"):
    val playerState = PlayerState(SortedBag.of[Ticket], SortedBag.of[Card], List.empty)
    for additionalCardsCount <- List(-1, 0, 4, 5, 6) do
      assertThrows[IllegalArgumentException]:
        playerState.possibleAdditionalCards(
          additionalCardsCount,
          SortedBag.of(Card.Blue),
          SortedBag.of(3, Card.Red)
        )

  test("playerState possibleAdditionalCards fails with invalid initial cards"):
    val playerState = PlayerState(SortedBag.of[Ticket], SortedBag.of[Card], List.empty)
    assertThrows[IllegalArgumentException]:
      playerState.possibleAdditionalCards(
        1,
        SortedBag.of[Card],
        SortedBag.of(3, Card.Red)
      )
    assertThrows[IllegalArgumentException]:
      playerState.possibleAdditionalCards(
        1,
        SortedBag.of(List(Card.Red, Card.Blue, Card.White)),
        SortedBag.of(3, Card.Red)
      )

  test("playerState possibleAdditionalCards fails with invalid drawn cards"):
    val playerState = PlayerState(SortedBag.of[Ticket], SortedBag.of[Card], List.empty)
    for drawnCardsCount <- List(0, 1, 2, 4, 5) do
      assertThrows[IllegalArgumentException]:
        playerState.possibleAdditionalCards(
          1,
          SortedBag.of(Card.Blue),
          SortedBag.of(drawnCardsCount, Card.Red)
        )

  test("playerState withClaimedRoute works"):
    val chMap = ChMap()
    var cards = sixOfKBYRL()

    var playerState = PlayerState(SortedBag.of[Ticket], cards, List.empty)
    val r1 = Route("AT2_VAD_1", chMap.AT2, chMap.VAD, 1, Level.Underground, Some(Color.Red))
    val c1 = SortedBag.of(1, Card.Locomotive)
    playerState = playerState.withClaimedRoute(r1, c1)
    cards = cards.difference(c1)
    assert(cards == playerState.cards)
    assert(Set(r1) == playerState.routes.toSet)

    val r2 = Route("BAL_OLT_1", chMap.BAL, chMap.OLT, 2, Level.Underground, Some(Color.Orange))
    val c2 = SortedBag.of(2, Card.Orange)
    playerState = playerState.withClaimedRoute(r2, c2)
    cards = cards.difference(c2)
    assert(cards == playerState.cards)
    assert(Set(r1, r2) == playerState.routes.toSet)

  private def routesWithTotalLength(length: Int): List[Route] =
    (0 until length).map: i =>
      val s1 = Station(2 * i, s"From$i")
      val s2 = Station(2 * i + 1, s"To$i")
      Route(s"r$i", s1, s2, 1, Level.Overground, Some(Color.Orange))
    .toList

  private def shuffledCards(rng: Random): List[Card] =
    val cards = sixOfEachCard()
    Random.shuffle(cards.toList)

  private def kbyr(): Set[Color] =
    Set(Color.Black, Color.Blue, Color.Yellow, Color.Red)

  private def sixOfKBYRL(): SortedBag[Card] =
    SortedBag.Builder[Card]()
      .add(6, Card.Black)
      .add(6, Card.Blue)
      .add(6, Card.Yellow)
      .add(6, Card.Red)
      .add(6, Card.Locomotive)
      .build()

  private def sixOfEachCard(): SortedBag[Card] =
    SortedBag.Builder[Card]()
      .add(6, Card.Black)
      .add(6, Card.Violet)
      .add(6, Card.Blue)
      .add(6, Card.Green)
      .add(6, Card.Yellow)
      .add(6, Card.Orange)
      .add(6, Card.Red)
      .add(6, Card.White)
      .add(6, Card.Locomotive)
      .build()

  private class ChMap:
    // Stations
    val BAL = Station(1, "Bâle")
    val BER = Station(3, "Berne")
    val AT2 = Station(40, "Autriche")
    val OLT = Station(20, "Olten")
    val VAD = Station(28, "Vaduz")

    // Routes
    val BER_LUC_1 = Route("BER_LUC_1", BER, Station(16, "Lucerne"), 4, Level.Overground, None)

    val ALL_ROUTES = List(BER_LUC_1)

    val ALL_TICKETS = List(
      Ticket(Station(0, "A"), Station(1, "B"), 5)
    )
