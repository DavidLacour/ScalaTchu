package ch.epfl.tchu.game

import ch.epfl.test.TestRandomizer
import org.scalatest.funsuite.AnyFunSuite

class TripTest extends AnyFunSuite:

  test("all works on known example"):
    val from = List(
      Station(0, "Lausanne"),
      Station(1, "Neuchâtel")
    )
    val to = List(
      Station(2, "Berne"),
      Station(3, "Zürich"),
      Station(4, "Coire")
    )
    val points = 17

    val expectedFromToIds = List(
      (0, 2), (0, 3), (0, 4),
      (1, 2), (1, 3), (1, 4)
    )
    val all = Trip.all(from, to, points)
    assert(from.size * to.size == all.size)

    for (fromId, toId) <- expectedFromToIds do
      val found = all.exists(trip => trip.from.id == fromId && trip.to.id == toId)
      assert(found, s"Missing trip from $fromId to $toId")

  test("constructor fails with invalid points"):
    val s1 = Station(0, "Lausanne")
    val s2 = Station(1, "EPFL")
    assertThrows[IllegalArgumentException]:
      Trip(s1, s2, 0)
    assertThrows[IllegalArgumentException]:
      Trip(s1, s2, -1)

  test("from, to and points return what they should"):
    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val fromId = rng.nextInt(100)
      val from = Station(fromId, "Lausanne")
      val to = Station(fromId + 1, "Neuchâtel")
      val points = 1 + rng.nextInt(10)
      val trip = Trip(from, to, points)
      assert(from == trip.from)
      assert(to == trip.to)
      assert(points == trip.points)

  test("points returns positive points when connected and negative otherwise"):
    val connected = new FullConnectivity
    val notConnected = new NoConnectivity

    val rng = TestRandomizer.newRandom()
    for _ <- 0 until TestRandomizer.RandomIterations do
      val fromId = rng.nextInt(100)
      val from = Station(fromId, "Lugano")
      val to = Station(fromId + 1, "Wassen")
      val points = 1 + rng.nextInt(10)
      val trip = Trip(from, to, points)
      assert(+points == trip.points(connected))
      assert(-points == trip.points(notConnected))

  private class FullConnectivity extends StationConnectivity:
    def connected(s1: Station, s2: Station): Boolean = true

  private class NoConnectivity extends StationConnectivity:
    def connected(s1: Station, s2: Station): Boolean = false
