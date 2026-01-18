package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument
import ch.epfl.tchu.SortedBag

/** Represents the complete state of a player (extends public state with private info).
  *
  * @param tickets the player's tickets
  * @param cards the player's cards
  * @param routes the routes claimed by the player
  */
final class PlayerState(
    val tickets: SortedBag[Ticket],
    val cards: SortedBag[Card],
    routes: List[Route]
) extends PublicPlayerState(tickets.size, cards.size, routes):

  /** Returns a new state with the given tickets added. */
  def withAddedTickets(newTickets: SortedBag[Ticket]): PlayerState =
    new PlayerState(tickets.union(newTickets), cards, routes)

  /** Returns a new state with the given card added. */
  def withAddedCard(card: Card): PlayerState =
    new PlayerState(tickets, cards.union(SortedBag.of(card)), routes)

  /** Returns a new state with the given cards added. */
  def withAddedCards(additionalCards: SortedBag[Card]): PlayerState =
    new PlayerState(tickets, cards.union(additionalCards), routes)

  /** Checks if the player can claim the given route. */
  def canClaimRoute(route: Route): Boolean =
    carCount >= route.length && possibleClaimCards(route).nonEmpty

  /** Returns all possible sets of cards the player can use to claim the route. */
  def possibleClaimCards(route: Route): List[SortedBag[Card]] =
    checkArgument(carCount >= route.length, "not enough cars")
    route.possibleClaimCards.filter(cards.contains)

  /** Returns all possible sets of additional cards the player can use for a tunnel claim. */
  def possibleAdditionalCards(
      additionalCardsCount: Int,
      initialCards: SortedBag[Card],
      drawnCards: SortedBag[Card]
  ): List[SortedBag[Card]] =
    checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.AdditionalTunnelCards,
      "invalid additional cards count")
    checkArgument(initialCards.nonEmpty, "initial cards cannot be empty")
    checkArgument(initialCards.toSet.size <= 2, "initial cards can have at most 2 types")
    checkArgument(drawnCards.size == Constants.AdditionalTunnelCards,
      s"must draw exactly ${Constants.AdditionalTunnelCards} cards")

    // Cards remaining after playing initial cards
    val remainingCards = cards.difference(initialCards)

    // Find wagon color used (if any)
    val wagonColor = CardUtils.wagonColor(initialCards)

    // Find usable cards: locomotives and the wagon color used (if any)
    val usableCards = SortedBag.of(remainingCards.filter { card =>
      card == Card.Locomotive || (wagonColor.isDefined && card.color == wagonColor)
    })

    // If not enough cards, return empty list
    if usableCards.size < additionalCardsCount then
      Nil
    else
      // Get all subsets of the required size, sorted by locomotive count
      usableCards.subsetsOfSize(additionalCardsCount)
        .toList
        .sortBy(_.countOf(Card.Locomotive))

  /** Returns a new state with the route claimed using the given cards. */
  def withClaimedRoute(route: Route, claimCards: SortedBag[Card]): PlayerState =
    new PlayerState(tickets, cards.difference(claimCards), routes :+ route)

  /** Returns the points from tickets (positive for connected, negative for not connected). */
  def ticketPoints: Int =
    // Find max station ID
    val maxStationId = routes.flatMap(r => List(r.station1.id, r.station2.id))
      .maxOption.getOrElse(0)

    // Build station partition
    val builder = StationPartition.Builder(maxStationId + 1)
    routes.foreach(route => builder.connect(route.station1, route.station2))
    val partition = builder.build()

    // Calculate ticket points
    tickets.map(_.points(partition)).sum

  /** Returns the total final points (claim points + ticket points). */
  def finalPoints: Int = claimPoints + ticketPoints

object PlayerState:
  /** Creates the initial player state with the given initial cards. */
  def initial(initialCards: SortedBag[Card]): PlayerState =
    checkArgument(initialCards.size == Constants.InitialCardsCount,
      s"must have exactly ${Constants.InitialCardsCount} cards")
    new PlayerState(SortedBag.of[Ticket], initialCards, Nil)
