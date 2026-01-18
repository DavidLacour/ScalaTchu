package ch.epfl.tchu.game

import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random

class PublicCardStateTest extends AnyFunSuite:

  private val FACE_UP_CARDS = List(Card.Blue, Card.Black, Card.Orange, Card.Orange, Card.Red)

  test("publicCardState constructor fails with invalid number of face up cards"):
    for i <- 0 until 10 if i != FACE_UP_CARDS.size do
      val faceUpCards = List.fill(i)(Card.Black)
      assertThrows[IllegalArgumentException]:
        PublicCardState(faceUpCards, 0, 0)

  test("constructor fails with negative deck or discards size"):
    assertThrows[IllegalArgumentException]:
      PublicCardState(FACE_UP_CARDS, -1, 0)
    assertThrows[IllegalArgumentException]:
      PublicCardState(FACE_UP_CARDS, 0, -1)

  test("constructor copies face up cards"):
    val faceUpCards = FACE_UP_CARDS.toBuffer
    val cardState = PublicCardState(faceUpCards.toList, 0, 0)
    faceUpCards.clear()
    assert(FACE_UP_CARDS == cardState.faceUpCards)

  test("totalSize returns total size"):
    for i <- 0 until 10 do
      for j <- 0 until 10 do
        val cardState = PublicCardState(FACE_UP_CARDS, i, j)
        val expectedTotal = i + j + FACE_UP_CARDS.size
        assert(expectedTotal == cardState.totalSize)

  test("faceUpCards returns immutable list or copy"):
    val cardState = PublicCardState(FACE_UP_CARDS, 0, 0)
    // In Scala, the list is immutable by default
    assert(FACE_UP_CARDS == cardState.faceUpCards)

  test("faceUpCard fails with invalid slot index"):
    val cardState = PublicCardState(FACE_UP_CARDS, 0, 0)
    // In Scala, we use checkArgument which throws IllegalArgumentException
    for i <- -20 until 0 do
      assertThrows[IllegalArgumentException]:
        cardState.faceUpCard(i)
    for i <- 6 to 20 do
      assertThrows[IllegalArgumentException]:
        cardState.faceUpCard(i)

  test("faceUpCard returns correct card"):
    val rng = TestRandomizer.newRandom()
    for i <- 0 until TestRandomizer.RandomIterations do
      val cards = Random.shuffle(Card.all)
      val faceUpCards = cards.take(5)
      val cardState = PublicCardState(faceUpCards, 0, 0)
      for j <- faceUpCards.indices do
        assert(faceUpCards(j) == cardState.faceUpCard(j))

  test("deckSize returns deck size"):
    for i <- 0 until 100 do
      val cardState = PublicCardState(FACE_UP_CARDS, i, i + 1)
      assert(i == cardState.deckSize)

  test("isDeckEmpty returns true only when deck empty"):
    assert(PublicCardState(FACE_UP_CARDS, 0, 1).isDeckEmpty)
    for i <- 0 until 100 do
      val cardState = PublicCardState(FACE_UP_CARDS, i + 1, i)
      assert(!cardState.isDeckEmpty)

  test("discardsSize returns discards size"):
    for i <- 0 until 100 do
      val cardState = PublicCardState(FACE_UP_CARDS, i + 1, i)
      assert(i == cardState.discardsSize)
