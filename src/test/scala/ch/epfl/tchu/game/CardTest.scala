package ch.epfl.tchu.game

import org.scalatest.funsuite.AnyFunSuite

class CardTest extends AnyFunSuite:

  test("card values are defined in the right order"):
    val expectedValues = List(
      Card.Black, Card.Violet, Card.Blue, Card.Green,
      Card.Yellow, Card.Orange, Card.Red, Card.White, Card.Locomotive
    )
    assert(expectedValues == Card.all)

  test("card all is defined correctly"):
    assert(Card.values.toList == Card.all)

  test("card count is defined correctly"):
    assert(Card.count == 9)

  test("card of works for all colors"):
    val allCards = Card.values
    for color <- Color.values do
      assert(allCards(color.ordinal) == Card.of(color))

  test("card color works for all colors"):
    val allColors = Color.values
    for card <- Card.values if card != Card.Locomotive do
      assert(Some(allColors(card.ordinal)) == card.color)
