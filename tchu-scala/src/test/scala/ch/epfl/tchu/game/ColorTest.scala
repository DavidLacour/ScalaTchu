package ch.epfl.tchu.game

import org.scalatest.funsuite.AnyFunSuite

class ColorTest extends AnyFunSuite:

  test("color values are defined in the right order"):
    val expectedValues = List(
      Color.Black, Color.Violet, Color.Blue, Color.Green,
      Color.Yellow, Color.Orange, Color.Red, Color.White
    )
    assert(expectedValues == Color.all)

  test("color all is defined correctly"):
    assert(Color.values.toList == Color.all)

  test("color count is defined correctly"):
    assert(Color.count == 8)
