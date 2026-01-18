package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument
import ch.epfl.tchu.SortedBag
import scala.util.Random

/** Represents the complete state of the game (extends public state with private info). */
final class GameState private (
    private val tickets: Deck[Ticket],
    private val privateCardState: CardState,
    currentPlayerId: PlayerId,
    private val privatePlayerState: Map[PlayerId, PlayerState],
    lastPlayer: Option[PlayerId]
) extends PublicGameState(
      tickets.size,
      privateCardState,
      currentPlayerId,
      privatePlayerState.map { case (id, ps) => id -> ps.asInstanceOf[PublicPlayerState] },
      lastPlayer
    ):

  /** Private copy method to reduce constructor repetition. */
  private def copy(
      tickets: Deck[Ticket] = this.tickets,
      cardState: CardState = this.privateCardState,
      currentPlayer: PlayerId = this.currentPlayerId,
      playerStates: Map[PlayerId, PlayerState] = this.privatePlayerState,
      lastPlayer: Option[PlayerId] = this.lastPlayer
  ): GameState = new GameState(tickets, cardState, currentPlayer, playerStates, lastPlayer)

  /** Private helper to update a player's state. */
  private def updatePlayer(id: PlayerId)(f: PlayerState => PlayerState): GameState =
    copy(playerStates = privatePlayerState.updated(id, f(privatePlayerState(id))))

  /** Private helper to update the current player's state. */
  private def updateCurrentPlayer(f: PlayerState => PlayerState): GameState =
    updatePlayer(currentPlayerId)(f)

  /** Returns the complete state of the given player. */
  override def playerState(playerId: PlayerId): PlayerState = privatePlayerState(playerId)

  /** Returns the complete state of the current player. */
  override def currentPlayerState: PlayerState = privatePlayerState(currentPlayerId)

  /** Returns the top tickets from the deck. */
  def topTickets(count: Int): SortedBag[Ticket] =
    checkArgument(count >= 0 && count <= tickets.size, "invalid count")
    tickets.topCards(count)

  /** Returns a new state without the top tickets. */
  def withoutTopTickets(count: Int): GameState =
    checkArgument(count >= 0 && count <= tickets.size, "invalid count")
    copy(tickets = tickets.withoutTopCards(count))

  /** Returns the top card of the deck. */
  def topCard: Card =
    checkArgument(!privateCardState.isDeckEmpty, "deck is empty")
    privateCardState.topDeckCard

  /** Returns a new state without the top deck card. */
  def withoutTopCard: GameState =
    checkArgument(!privateCardState.isDeckEmpty, "deck is empty")
    copy(cardState = privateCardState.withoutTopDeckCard)

  /** Returns a new state with the given discarded cards added. */
  def withMoreDiscardedCards(discardedCards: SortedBag[Card]): GameState =
    copy(cardState = privateCardState.withMoreDiscardedCards(discardedCards))

  /** Returns a new state with deck recreated from discards if deck is empty. */
  def withCardsDeckRecreatedIfNeeded(rng: Random): GameState =
    if privateCardState.isDeckEmpty then
      copy(cardState = privateCardState.withDeckRecreatedFromDiscards(rng))
    else
      this

  /** Returns a new state with the given tickets added to the player. */
  def withInitiallyChosenTickets(playerId: PlayerId, chosenTickets: SortedBag[Ticket]): GameState =
    checkArgument(privatePlayerState(playerId).tickets.isEmpty, "player already has tickets")
    updatePlayer(playerId)(_.withAddedTickets(chosenTickets))

  /** Returns a new state with drawn tickets kept by the current player. */
  def withChosenAdditionalTickets(drawnTickets: SortedBag[Ticket], chosenTickets: SortedBag[Ticket]): GameState =
    checkArgument(drawnTickets.contains(chosenTickets), "drawn tickets must contain chosen tickets")
    updateCurrentPlayer(_.withAddedTickets(chosenTickets))
      .copy(tickets = tickets.withoutTopCards(drawnTickets.size))

  /** Returns a new state with a face-up card drawn by the current player. */
  def withDrawnFaceUpCard(slot: Int): GameState =
    checkArgument(canDrawCards, "cannot draw cards")
    val drawnCard = privateCardState.faceUpCard(slot)
    updateCurrentPlayer(_.withAddedCard(drawnCard))
      .copy(cardState = privateCardState.withDrawnFaceUpCard(slot))

  /** Returns a new state with a blind card drawn by the current player. */
  def withBlindlyDrawnCard: GameState =
    checkArgument(canDrawCards, "cannot draw cards")
    val drawnCard = privateCardState.topDeckCard
    updateCurrentPlayer(_.withAddedCard(drawnCard))
      .copy(cardState = privateCardState.withoutTopDeckCard)

  /** Returns a new state with a route claimed by the current player. */
  def withClaimedRoute(route: Route, cards: SortedBag[Card]): GameState =
    updateCurrentPlayer(_.withClaimedRoute(route, cards))
      .copy(cardState = privateCardState.withMoreDiscardedCards(cards))

  /** Returns whether the last turn begins (current player has 2 or fewer cars). */
  def lastTurnBegins: Boolean = lastPlayer.isEmpty && currentPlayerState.carCount <= 2

  /** Returns a new state for the next turn. */
  def forNextTurn: GameState =
    val newLastPlayer = if lastTurnBegins then Some(currentPlayerId) else lastPlayer
    copy(currentPlayer = currentPlayerId.next, lastPlayer = newLastPlayer)

object GameState:
  /** Creates the initial game state with the given tickets and random generator. */
  def initial(tickets: SortedBag[Ticket], rng: Random): GameState =
    var ticketDeck = Deck.of(tickets, rng)
    var cardDeck = Deck.of(Constants.AllCards, rng)

    // Deal initial cards to each player
    val playerStates = PlayerId.all.map { id =>
      val initialCards = cardDeck.topCards(Constants.InitialCardsCount)
      cardDeck = cardDeck.withoutTopCards(Constants.InitialCardsCount)
      id -> PlayerState.initial(initialCards)
    }.toMap

    // Randomly choose first player
    val firstPlayer = PlayerId.all(rng.nextInt(PlayerId.count))

    val cardState = CardState.of(cardDeck)

    new GameState(ticketDeck, cardState, firstPlayer, playerStates, None)
