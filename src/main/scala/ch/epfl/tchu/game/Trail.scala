package ch.epfl.tchu.game

/** Represents a trail (path) in a player's network.
  * A trail is a sequence of connected routes without repetition.
  */
final class Trail private (
    val station1: Option[Station],
    val station2: Option[Station],
    val routes: List[Route]
):
  /** Returns the length of this trail (sum of route lengths). */
  val length: Int = routes.map(_.length).sum

  override def toString: String =
    if length == 0 || station1.isEmpty || station2.isEmpty then
      "Empty trail (0)"
    else
      val sb = StringBuilder()
      sb.append(station1.get.name)

      var current = station1.get
      for route <- routes do
        val next = route.stationOpposite(current)
        sb.append(" - ").append(next.name)
        current = next

      sb.append(" (").append(length).append(")")
      sb.toString

object Trail:
  /** Creates an empty trail. */
  private def empty: Trail = new Trail(None, None, Nil)

  /** Finds and returns the longest trail in the given network of routes. */
  def longest(routes: List[Route]): Trail =
    if routes.isEmpty then return empty

    var longestTrail = empty

    // Initialize with single-route trails (in both directions)
    var currentTrails = routes.flatMap { route =>
      List(
        new Trail(Some(route.station1), Some(route.station2), List(route)),
        new Trail(Some(route.station2), Some(route.station1), List(route))
      )
    }

    while currentTrails.nonEmpty do
      // Check for longest in current batch
      for trail <- currentTrails do
        if trail.length > longestTrail.length then
          longestTrail = trail

      // Try to extend each trail
      currentTrails = currentTrails.flatMap { trail =>
        routes
          .filterNot(trail.routes.contains)
          .flatMap { route =>
            val trailEnd = trail.station2.get
            if route.station1 == trailEnd then
              Some(new Trail(trail.station1, Some(route.station2), trail.routes :+ route))
            else if route.station2 == trailEnd then
              Some(new Trail(trail.station1, Some(route.station1), trail.routes :+ route))
            else
              None
          }
      }

    longestTrail
