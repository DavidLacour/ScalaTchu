package ch.epfl.tchu

import org.scalatest.funsuite.AnyFunSuite

class PreconditionsTest extends AnyFunSuite:

  test("checkArgument succeeds for true"):
    Preconditions.checkArgument(true)

  test("checkArgument fails for false"):
    assertThrows[IllegalArgumentException]:
      Preconditions.checkArgument(false)
