package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument

/** Represents the public (visible) part of the game state.
  *
  * @param ticketsCount the number of tickets remaining in the deck
  * @param cardState the public card state
  * @param currentPlayerId the current player
  * @param playerStates the public state of each player
  * @param lastPlayer the last player (None if not yet determined)
  */
class PublicGameState(
    val ticketsCount: Int,
    val cardState: PublicCardState,
    val currentPlayerId: PlayerId,
    protected val playerStates: Map[PlayerId, PublicPlayerState],
    val lastPlayer: Option[PlayerId]
):
  checkArgument(ticketsCount >= 0, "tickets count must be non-negative")
  checkArgument(playerStates.size == PlayerId.count, s"must have ${PlayerId.count} players")

  /** Returns whether tickets can be drawn (deck is not empty). */
  def canDrawTickets: Boolean = ticketsCount > 0

  /** Returns whether cards can be drawn (deck + discards >= 5). */
  def canDrawCards: Boolean = cardState.deckSize + cardState.discardsSize >= 5

  /** Returns the public state of the given player. */
  def playerState(playerId: PlayerId): PublicPlayerState = playerStates(playerId)

  /** Returns the public state of the current player. */
  def currentPlayerState: PublicPlayerState = playerStates(currentPlayerId)

  /** Returns all routes claimed by either player. */
  def claimedRoutes: List[Route] = playerStates.values.flatMap(_.routes).toList
