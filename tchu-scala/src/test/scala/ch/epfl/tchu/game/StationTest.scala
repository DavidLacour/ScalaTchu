package ch.epfl.tchu.game

import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite

class StationTest extends AnyFunSuite:

  test("station constructor fails for negative id"):
    assertThrows[IllegalArgumentException]:
      Station(-1, "Lausanne")

  test("id accessor works"):
    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val id = rng.nextInt(Int.MaxValue)
      val station = Station(id, "Lausanne")
      assert(id == station.id)

  private val alphabet = "abcdefghijklmnopqrstuvwxyz"

  private def randomName(rng: scala.util.Random, length: Int): String =
    val sb = new StringBuilder()
    for _ <- 0 until length do
      sb.append(alphabet.charAt(rng.nextInt(alphabet.length)))
    sb.toString()

  test("name accessor works"):
    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val name = randomName(rng, 1 + rng.nextInt(10))
      val station = Station(1, name)
      assert(name == station.name)

  test("station toString returns name"):
    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val name = randomName(rng, 1 + rng.nextInt(10))
      val station = Station(1, name)
      assert(name == station.toString)
