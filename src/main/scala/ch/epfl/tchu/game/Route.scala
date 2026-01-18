package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument
import ch.epfl.tchu.SortedBag

/** Represents the level (surface or tunnel) of a route. */
enum Level:
  case Overground, Underground

/** Represents a route connecting two stations in the game.
  *
  * @param id the unique identifier of the route
  * @param station1 the first station
  * @param station2 the second station
  * @param length the length of the route (number of cells)
  * @param level the level (Overground or Underground)
  * @param color the color of the route, or None if neutral
  */
final case class Route(
    id: String,
    station1: Station,
    station2: Station,
    length: Int,
    level: Level,
    color: Option[Color]
):
  checkArgument(station1 != station2, "stations must be different")
  checkArgument(length >= Constants.MinRouteLength && length <= Constants.MaxRouteLength,
    s"length must be between ${Constants.MinRouteLength} and ${Constants.MaxRouteLength}")

  /** Returns a list containing both stations of this route. */
  def stations: List[Station] = List(station1, station2)

  /** Returns the station opposite to the given station. */
  def stationOpposite(station: Station): Station =
    checkArgument(station == station1 || station == station2, "station must be one of the route's stations")
    if station == station1 then station2 else station1

  /** Returns all possible sets of cards that could be used to claim this route. */
  def possibleClaimCards: List[SortedBag[Card]] =
    val possibilities = scala.collection.mutable.ListBuffer[SortedBag[Card]]()

    level match
      case Level.Overground =>
        // Surface route: only wagon cards, no locomotives
        color match
          case None =>
            // Neutral route: any single color works
            Card.cars.foreach(card => possibilities += SortedBag.of(length, card))
          case Some(c) =>
            // Colored route: only matching color
            possibilities += SortedBag.of(length, Card.of(c))

      case Level.Underground =>
        // Tunnel: can use locomotives
        for locomotives <- 0 to length do
          val wagons = length - locomotives
          color match
            case None =>
              // Neutral tunnel
              if wagons == 0 then
                possibilities += SortedBag.of(length, Card.Locomotive)
              else
                Card.cars.foreach { card =>
                  possibilities += SortedBag.of(wagons, card, locomotives, Card.Locomotive)
                }
            case Some(c) =>
              // Colored tunnel
              val wagonCard = Card.of(c)
              possibilities += SortedBag.of(wagons, wagonCard, locomotives, Card.Locomotive)

    possibilities.toList

  /** Calculates the number of additional cards needed to claim a tunnel. */
  def additionalClaimCardsCount(claimCards: SortedBag[Card], drawnCards: SortedBag[Card]): Int =
    checkArgument(level == Level.Underground, "route must be a tunnel")
    checkArgument(drawnCards.size == Constants.AdditionalTunnelCards,
      s"must draw exactly ${Constants.AdditionalTunnelCards} cards")

    // Find the color used in claim cards (if any wagon cards used)
    val claimColor = CardUtils.wagonColor(claimCards)

    // Count matching cards in drawn cards
    drawnCards.count { drawnCard =>
      drawnCard == Card.Locomotive || (claimColor.isDefined && drawnCard.color == claimColor)
    }

  /** Returns the claim points for this route based on its length. */
  def claimPoints: Int = Constants.RouteClaimPoints(length)
