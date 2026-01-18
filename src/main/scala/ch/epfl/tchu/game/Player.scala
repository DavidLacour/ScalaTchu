package ch.epfl.tchu.game

import ch.epfl.tchu.SortedBag

/** Enum representing the possible types of turns. */
enum TurnKind:
  case DrawTickets, DrawCards, ClaimRoute

object TurnKind:
  val all: List[TurnKind] = TurnKind.values.toList

/** Interface representing a player in the game.
  * A player makes decisions during the game and is notified of game events.
  */
trait Player:
  /** Called at the start of the game to inform the player of their identity
    * and the names of all players.
    */
  def initPlayers(ownId: PlayerId, playerNames: Map[PlayerId, String]): Unit

  /** Called to inform the player of a piece of information. */
  def receiveInfo(info: String): Unit

  /** Called whenever the game state changes to inform the player. */
  def updateState(newState: PublicGameState, ownState: PlayerState): Unit

  /** Called at the start of the game to give the player their initial tickets. */
  def setInitialTicketChoice(tickets: SortedBag[Ticket]): Unit

  /** Called to ask the player which initial tickets they want to keep. */
  def chooseInitialTickets(): SortedBag[Ticket]

  /** Called at the start of each turn to ask the player what kind of turn they want to play. */
  def nextTurn(): TurnKind

  /** Called when the player draws tickets to let them choose which to keep. */
  def chooseTickets(options: SortedBag[Ticket]): SortedBag[Ticket]

  /** Called when the player draws cards to know from where they want to draw.
    * Returns Constants.DeckSlot (-1) for deck, or 0-4 for face-up cards.
    */
  def drawSlot(): Int

  /** Called when the player claims a route to know which route they want. */
  def claimedRoute(): Route

  /** Called when the player claims a route to know which cards they want to use. */
  def initialClaimCards(): SortedBag[Card]

  /** Called when claiming a tunnel to know which additional cards to use.
    * Returns empty bag if the player abandons.
    */
  def chooseAdditionalCards(options: List[SortedBag[Card]]): SortedBag[Card]
