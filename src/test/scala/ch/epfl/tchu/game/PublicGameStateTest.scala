package ch.epfl.tchu.game

import ch.epfl.tchu.SortedBag
import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random

class PublicGameStateTest extends AnyFunSuite:

  test("publicGameState constructor fails with invalid tickets count"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState,
      PlayerId.Player2 -> initialPlayerState
    )
    for i <- -10 until 0 do
      assertThrows[IllegalArgumentException]:
        PublicGameState(i, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))

  test("publicGameState constructor fails with invalid player state"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(PlayerId.Player1 -> initialPlayerState)
    assertThrows[IllegalArgumentException]:
      PublicGameState(1, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))

  test("publicGameState ticketsCount returns tickets count"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState,
      PlayerId.Player2 -> initialPlayerState
    )
    for ticketsCount <- 0 until 10 do
      val pgs = PublicGameState(ticketsCount, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))
      assert(ticketsCount == pgs.ticketsCount)

  test("publicGameState canDrawTickets works"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState,
      PlayerId.Player2 -> initialPlayerState
    )
    for ticketsCount <- 0 until 10 do
      val pgs = PublicGameState(ticketsCount, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))
      val canDraw = ticketsCount > 0
      assert(canDraw == pgs.canDrawTickets)

  test("publicGameState cardState returns card state"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState,
      PlayerId.Player2 -> initialPlayerState
    )
    val pgs = PublicGameState(1, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))
    assert(cardState == pgs.cardState)

  test("publicGameState canDrawCards works"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val initialPlayerState = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState,
      PlayerId.Player2 -> initialPlayerState
    )
    for totalCards <- 0 until 10 do
      val canDraw = totalCards >= 5
      for deckSize <- 0 to totalCards do
        val discardsSize = totalCards - deckSize
        val cardState = PublicCardState(faceUpCards, deckSize, discardsSize)
        val pgs = PublicGameState(1, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))
        assert(canDraw == pgs.canDrawCards)

  test("publicGameState currentPlayerId returns current player id"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState,
      PlayerId.Player2 -> initialPlayerState
    )
    for playerId <- List(PlayerId.Player1, PlayerId.Player2) do
      val pgs = PublicGameState(1, cardState, playerId, playerState, Some(PlayerId.Player1))
      assert(playerId == pgs.currentPlayerId)

  test("publicGameState playerState returns player state"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState1 = PlayerState.initial(SortedBag.of(4, Card.Red))
    val initialPlayerState2 = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState1,
      PlayerId.Player2 -> initialPlayerState2
    )
    val pgs = PublicGameState(1, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))
    assert(initialPlayerState1 == pgs.playerState(PlayerId.Player1))
    assert(initialPlayerState2 == pgs.playerState(PlayerId.Player2))

  test("publicGameState currentPlayerState returns current player state"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState1 = PlayerState.initial(SortedBag.of(4, Card.Red))
    val initialPlayerState2 = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState1,
      PlayerId.Player2 -> initialPlayerState2
    )
    val pgs1 = PublicGameState(1, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))
    assert(initialPlayerState1 == pgs1.currentPlayerState)
    val pgs2 = PublicGameState(1, cardState, PlayerId.Player2, playerState, Some(PlayerId.Player1))
    assert(initialPlayerState2 == pgs2.currentPlayerState)

  test("publicGameState claimedRoutes returns claimed routes"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val routes = ChMap().ALL_ROUTES
    val maxRoutesPerPlayer = routes.size / 2
    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val shuffled = Random.shuffle(routes)
      val n1 = rng.nextInt(maxRoutesPerPlayer)
      val n2 = rng.nextInt(maxRoutesPerPlayer)

      val routes1 = shuffled.take(n1)
      val routes2 = shuffled.slice(n1, n1 + n2)
      val routes12 = (routes1 ++ routes2).toSet

      val playerState = Map(
        PlayerId.Player1 -> PublicPlayerState(0, 0, routes1),
        PlayerId.Player2 -> PublicPlayerState(0, 0, routes2)
      )

      val pgs = PublicGameState(1, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))
      assert(routes12 == pgs.claimedRoutes.toSet)

  test("publicGameState lastPlayer returns last player"):
    val faceUpCards = SortedBag.of(5, Card.Locomotive).toList
    val cardState = PublicCardState(faceUpCards, 0, 0)
    val initialPlayerState = PlayerState.initial(SortedBag.of(4, Card.Red))
    val playerState = Map(
      PlayerId.Player1 -> initialPlayerState,
      PlayerId.Player2 -> initialPlayerState
    )
    val pgs1 = PublicGameState(1, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player1))
    assert(Some(PlayerId.Player1) == pgs1.lastPlayer)
    val pgs2 = PublicGameState(1, cardState, PlayerId.Player1, playerState, Some(PlayerId.Player2))
    assert(Some(PlayerId.Player2) == pgs2.lastPlayer)
    val pgsN = PublicGameState(1, cardState, PlayerId.Player1, playerState, None)
    assert(None == pgsN.lastPlayer)

  private class ChMap:
    // Stations
    val BAD = Station(0, "Baden")
    val BAL = Station(1, "Bâle")
    val BER = Station(3, "Berne")
    val FRI = Station(9, "Fribourg")
    val GEN = Station(10, "Genève")
    val INT = Station(11, "Interlaken")
    val LAU = Station(13, "Lausanne")
    val LUC = Station(16, "Lucerne")
    val OLT = Station(20, "Olten")
    val SOL = Station(26, "Soleure")
    val ZUR = Station(33, "Zürich")

    // Routes
    val BER_FRI_1 = Route("BER_FRI_1", BER, FRI, 1, Level.Overground, Some(Color.Orange))
    val BER_LUC_1 = Route("BER_LUC_1", BER, LUC, 4, Level.Overground, None)
    val GEN_LAU_1 = Route("GEN_LAU_1", GEN, LAU, 4, Level.Overground, Some(Color.Blue))
    val INT_LUC_1 = Route("INT_LUC_1", INT, LUC, 4, Level.Overground, Some(Color.Violet))
    val LUC_OLT_1 = Route("LUC_OLT_1", LUC, OLT, 3, Level.Overground, Some(Color.Green))
    val OLT_SOL_1 = Route("OLT_SOL_1", OLT, SOL, 1, Level.Overground, Some(Color.Blue))
    val OLT_ZUR_1 = Route("OLT_ZUR_1", OLT, ZUR, 3, Level.Overground, Some(Color.White))
    val BAD_ZUR_1 = Route("BAD_ZUR_1", BAD, ZUR, 1, Level.Overground, Some(Color.Yellow))

    val ALL_ROUTES = List(
      BER_FRI_1, BER_LUC_1, GEN_LAU_1, INT_LUC_1,
      LUC_OLT_1, OLT_SOL_1, OLT_ZUR_1, BAD_ZUR_1
    )
