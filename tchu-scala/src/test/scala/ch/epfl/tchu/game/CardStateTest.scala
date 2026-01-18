package ch.epfl.tchu.game

import ch.epfl.tchu.SortedBag
import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random

class CardStateTest extends AnyFunSuite:

  private val ALL_CARDS = Card.all
  private val FACE_UP_CARDS_COUNT = 5

  test("cardState of fails if deck is too small"):
    for i <- 0 until FACE_UP_CARDS_COUNT do
      val deck = Deck.of(SortedBag.of(i, Card.Red), Random(i))
      assertThrows[IllegalArgumentException]:
        CardState.of(deck)

  test("cardState of correctly draws face up cards"):
    val cards = allCards()

    for i <- 0 until 10 do
      val deck = Deck.of(cards, Random(i))

      var deck1 = deck
      var top5 = List.empty[Card]
      for _ <- 0 until 5 do
        top5 = top5 :+ deck1.topCard
        deck1 = deck1.withoutTopCard

      val cardState = CardState.of(deck)
      var faceUpCards = cardState.faceUpCards

      // Sort the cards, as the assignment was not explicit about preserving order
      top5 = top5.sorted
      faceUpCards = faceUpCards.sorted

      assert(top5 == faceUpCards)
      assert(deck.size - 5 == cardState.deckSize)
      assert(0 == cardState.discardsSize)

  test("cardState withDrawnFaceUpCard correctly replaces it"):
    val cards = allCards()

    for i <- 0 until 10 do
      val deck = Deck.of(cards, Random(-i))

      var deck1 = deck.withoutTopCards(5)
      var next5 = List.empty[Card]
      for _ <- 0 until 5 do
        next5 = next5 :+ deck1.topCard
        deck1 = deck1.withoutTopCard

      var cardState = CardState.of(deck)
      val next5It = next5.iterator
      val slots = Random.shuffle(List(0, 1, 2, 3, 4))
      for slot <- slots do
        cardState = cardState.withDrawnFaceUpCard(slot)
        assert(next5It.next() == cardState.faceUpCard(slot))

  test("cardState topDeckCard fails with empty deck"):
    val cardState = CardState.of(Deck.of(SortedBag.of(5, Card.Orange), TestRandomizer.newRandom()))
    assertThrows[IllegalArgumentException]:
      cardState.topDeckCard

  test("cardState topDeckCard returns top deck card"):
    val cards = allCards()
    for i <- 0 until 10 do
      val deck = Deck.of(cards, Random((i + 35) * 7))
      val topDeckCard = deck.withoutTopCards(5).topCard
      val cardState = CardState.of(deck)
      assert(topDeckCard == cardState.topDeckCard)

  test("cardState withoutTopDeckCard fails with empty deck"):
    val cardState = CardState.of(Deck.of(SortedBag.of(5, Card.Orange), TestRandomizer.newRandom()))
    assertThrows[IllegalArgumentException]:
      cardState.withoutTopDeckCard

  test("cardState withoutTopDeckCard works"):
    val cards = allCards()

    for i <- 0 until 10 do
      val deck = Deck.of(cards, Random(2021 - i))

      var expectedCards = List.empty[Card]
      var deck1 = deck.withoutTopCards(5)
      while !deck1.isEmpty do
        expectedCards = expectedCards :+ deck1.topCard
        deck1 = deck1.withoutTopCard

      var actualCards = List.empty[Card]
      var cardState = CardState.of(deck)
      while !cardState.isDeckEmpty do
        actualCards = actualCards :+ cardState.topDeckCard
        cardState = cardState.withoutTopDeckCard

      assert(expectedCards == actualCards)

  test("cardState withDeckRecreatedFromDiscards fails when deck is not empty"):
    val deck = Deck.of(SortedBag.of(6, Card.Red), TestRandomizer.newRandom())
    val cardState = CardState.of(deck)
    assertThrows[IllegalArgumentException]:
      cardState.withDeckRecreatedFromDiscards(TestRandomizer.newRandom())

  test("cardState withDeckRecreatedFromDiscards works with empty discards"):
    val deck = Deck.of(
      SortedBag.of(FACE_UP_CARDS_COUNT, Card.Red),
      TestRandomizer.newRandom()
    )
    val cardState = CardState.of(deck)
    val cardState1 = cardState.withDeckRecreatedFromDiscards(TestRandomizer.newRandom())
    assert(0 == cardState1.deckSize)
    assert(0 == cardState1.discardsSize)

  test("cardState withDeckRecreatedFromDiscards works with non-empty discards"):
    val deck = Deck.of(
      SortedBag.of(FACE_UP_CARDS_COUNT, Card.Red),
      TestRandomizer.newRandom()
    )
    val discardsCount = 10
    val discards = SortedBag.of(discardsCount, Card.Blue)
    var cardState = CardState.of(deck)
      .withMoreDiscardedCards(discards)
      .withDeckRecreatedFromDiscards(TestRandomizer.newRandom())
    assert(discardsCount == cardState.deckSize)
    val deckCardsBuilder = SortedBag.Builder[Card]()
    for _ <- 0 until discardsCount do
      val topDeckCard = cardState.topDeckCard
      cardState = cardState.withoutTopDeckCard
      deckCardsBuilder.add(topDeckCard)
    assert(cardState.isDeckEmpty)
    assert(discards == deckCardsBuilder.build())

  test("cardState withMoreDiscardedCards works"):
    val rng = TestRandomizer.newRandom()
    val deck = Deck.of(
      SortedBag.of(FACE_UP_CARDS_COUNT, Card.Red),
      TestRandomizer.newRandom()
    )
    val expectedDeckBuilder = SortedBag.Builder[Card]()
    var cardState = CardState.of(deck)
    for card <- ALL_CARDS do
      val count = rng.nextInt(12)
      val discards = SortedBag.of(count, card)
      cardState = cardState.withMoreDiscardedCards(discards)
      expectedDeckBuilder.add(count, card)

    cardState = cardState.withDeckRecreatedFromDiscards(Random(rng.nextLong()))
    val expectedDeck = expectedDeckBuilder.build()

    val actualDeckBuilder = SortedBag.Builder[Card]()
    for _ <- 0 until expectedDeck.size do
      val topDeckCard = cardState.topDeckCard
      cardState = cardState.withoutTopDeckCard
      actualDeckBuilder.add(topDeckCard)
    assert(cardState.isDeckEmpty)
    assert(expectedDeck == actualDeckBuilder.build())

  private def allCards(): SortedBag[Card] =
    val cardsBuilder = SortedBag.Builder[Card]()
    cardsBuilder.add(14, Card.Locomotive)
    for card <- Card.cars do
      cardsBuilder.add(12, card)
    cardsBuilder.build()
