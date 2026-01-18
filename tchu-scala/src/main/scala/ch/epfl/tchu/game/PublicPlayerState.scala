package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument

/** Represents the public (visible) part of a player's state.
  *
  * @param ticketCount the number of tickets
  * @param cardCount the number of cards
  * @param routes the routes claimed by the player
  */
class PublicPlayerState(
    val ticketCount: Int,
    val cardCount: Int,
    val routes: List[Route]
):
  checkArgument(ticketCount >= 0, "ticket count must be non-negative")
  checkArgument(cardCount >= 0, "card count must be non-negative")

  /** The number of cars (wagons) the player has. */
  val carCount: Int = Constants.InitialCarCount - routes.map(_.length).sum

  /** The claim points earned from routes. */
  val claimPoints: Int = routes.map(_.claimPoints).sum
