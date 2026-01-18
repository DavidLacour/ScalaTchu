package ch.epfl.tchu.game

import org.scalatest.funsuite.AnyFunSuite

class PlayerIdTest extends AnyFunSuite:

  test("playerId all is defined correctly"):
    assert(List(PlayerId.Player1, PlayerId.Player2) == PlayerId.all)

  test("playerId next works"):
    assert(PlayerId.Player2 == PlayerId.Player1.next)
    assert(PlayerId.Player1 == PlayerId.Player2.next)
