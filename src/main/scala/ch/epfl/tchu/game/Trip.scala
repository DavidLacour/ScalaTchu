package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument

/** Represents a trip between two stations with associated points.
  *
  * @param from the departure station
  * @param to the arrival station
  * @param points the number of points for this trip (must be positive)
  */
final case class Trip(from: Station, to: Station, points: Int):
  checkArgument(points > 0, s"points must be positive, got $points")

  /** Returns the points for this trip based on the given connectivity.
    * Returns positive points if stations are connected, negative otherwise.
    */
  def points(connectivity: StationConnectivity): Int =
    if connectivity.connected(from, to) then points else -points

object Trip:
  /** Creates a list of all possible trips from stations in the first list
    * to stations in the second list, each worth the given points.
    */
  def all(from: List[Station], to: List[Station], points: Int): List[Trip] =
    checkArgument(from.nonEmpty && to.nonEmpty, "station lists must not be empty")
    checkArgument(points > 0, "points must be positive")
    for
      fromStation <- from
      toStation <- to
    yield Trip(fromStation, toStation, points)
