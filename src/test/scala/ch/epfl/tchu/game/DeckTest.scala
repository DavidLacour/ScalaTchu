package ch.epfl.tchu.game

import ch.epfl.tchu.SortedBag
import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random

class DeckTest extends AnyFunSuite:

  test("deck of shuffles deck"):
    val cards = listOfSize(100)
    val cardsBag = SortedBag.of(cards)
    for i <- 0 until TestRandomizer.RandomIterations do
      val rng = Random(i)
      val deck = Deck.of(cardsBag, rng)
      val deckList = deckToList(deck)
      // The shuffled deck *could* be equal to the non-shuffled one,
      // but with 100 elements this is extremely unlikely
      assert(cards != deckList)

      val deckSortedList = deckList.sorted
      assert(cards == deckSortedList)

  test("deck size returns size"):
    for size <- 0 until 100 do
      val rng = Random(size)
      val deck = Deck.of(SortedBag.of(listOfSize(size)), rng)
      assert(size == deck.size)

  test("deck isEmpty returns true only when deck is empty"):
    for size <- 0 until 10 do
      val rng = Random(size)
      val deck = Deck.of(SortedBag.of(listOfSize(size)), rng)
      assert((size == 0) == deck.isEmpty)

  test("deck topCard fails with empty deck"):
    val deck = Deck.of(SortedBag.of[String], Random(2021))
    assertThrows[IllegalArgumentException]:
      deck.topCard

  test("deck topCard returns top card"):
    val card = "card"
    val deck = Deck.of(SortedBag.of(10, card), Random(2021))
    assert(card == deck.topCard)

  test("deck withoutTopCard fails with empty deck"):
    val deck = Deck.of(SortedBag.of[String], Random(2021))
    assertThrows[IllegalArgumentException]:
      deck.withoutTopCard

  test("deck withoutTopCard removes top card"):
    val cards = SortedBag.of(listOfSize(50))
    var deck = Deck.of(cards, TestRandomizer.newRandom())
    val actualCardsBuilder = SortedBag.Builder[Int]()
    while !deck.isEmpty do
      actualCardsBuilder.add(deck.topCard)
      deck = deck.withoutTopCard
    assert(cards == actualCardsBuilder.build())

  test("deck topCards fails with too small deck"):
    val rng = TestRandomizer.newRandom()
    for deckSize <- 0 until 20 do
      val deck = Deck.of(SortedBag.of(deckSize, "card"), rng)
      val tooBigSize = deckSize + 1
      assertThrows[IllegalArgumentException]:
        deck.withoutTopCards(tooBigSize)

  test("deck topCards returns top cards"):
    val rng = TestRandomizer.newRandom()
    for deckSize <- 0 until 20 do
      val cards = SortedBag.of(deckSize, "card")
      val deck = Deck.of(cards, rng)
      assert(cards == deck.topCards(deckSize))

  test("deck withoutTopCards removes top cards"):
    val cards = SortedBag.of(listOfSize((1 << 8) - 1))
    var deck = Deck.of(cards, TestRandomizer.newRandom())
    val actualCardsBuilder = SortedBag.Builder[Int]()
    for i <- 0 until 8 do
      val count = 1 << i // == 2^i
      actualCardsBuilder.add(deck.topCards(count))
      deck = deck.withoutTopCards(count)
    assert(deck.isEmpty)
    assert(cards == actualCardsBuilder.build())

  private def deckToList[E: Ordering](deck: Deck[E]): List[E] =
    var d = deck
    var list = List.empty[E]
    while !d.isEmpty do
      list = list :+ d.topCard
      d = d.withoutTopCard
    list

  private def listOfSize(size: Int): List[Int] =
    (0 until size).toList
