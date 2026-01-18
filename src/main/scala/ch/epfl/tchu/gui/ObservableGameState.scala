package ch.epfl.tchu.gui

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.*
import javafx.beans.property.*
import javafx.collections.{FXCollections, ObservableList}
import scala.jdk.CollectionConverters.*
import scala.compiletime.uninitialized

/** Observable game state for JavaFX GUI binding. */
final class ObservableGameState(val playerId: PlayerId):

  // Public game state properties
  private val _ticketPercentage = new SimpleIntegerProperty()
  private val _cardPercentage = new SimpleIntegerProperty()
  private val _faceUpCards = (0 until Constants.FaceUpCardsCount).map(_ => new SimpleObjectProperty[Card]()).toList
  private val _routeOwner = ChMap.routes.map(r => r -> new SimpleObjectProperty[PlayerId]()).toMap

  // Player state properties (for each player)
  private val _ticketCount = PlayerId.all.map(id => id -> new SimpleIntegerProperty(0)).toMap
  private val _cardCount = PlayerId.all.map(id => id -> new SimpleIntegerProperty(0)).toMap
  private val _carCount = PlayerId.all.map(id => id -> new SimpleIntegerProperty(0)).toMap
  private val _claimPoints = PlayerId.all.map(id => id -> new SimpleIntegerProperty(0)).toMap

  // Own player state properties
  private val _tickets: ObservableList[Ticket] = FXCollections.observableArrayList()
  private val _cardCountByType = Card.all.map(c => c -> new SimpleIntegerProperty(0)).toMap
  private val _canClaimRoute = ChMap.routes.map(r => r -> new SimpleBooleanProperty(false)).toMap

  // Game state
  private var _publicGameState: PublicGameState = uninitialized
  private var _playerState: PlayerState = uninitialized

  /** Updates the state with the given game state and player state. */
  def setState(publicGameState: PublicGameState, playerState: PlayerState): Unit =
    _publicGameState = publicGameState
    _playerState = playerState

    // Update ticket and card percentages
    val totalTickets = ChMap.tickets.size
    _ticketPercentage.set(publicGameState.ticketsCount * 100 / totalTickets)

    val totalCards = Constants.TotalCardsCount
    val deckAndDiscards = publicGameState.cardState.deckSize + publicGameState.cardState.discardsSize
    _cardPercentage.set(deckAndDiscards * 100 / totalCards)

    // Update face-up cards
    for i <- 0 until Constants.FaceUpCardsCount do
      _faceUpCards(i).set(publicGameState.cardState.faceUpCard(i))

    // Update route owners
    for route <- ChMap.routes do
      val owner = PlayerId.all.find(id => publicGameState.playerState(id).routes.contains(route))
      _routeOwner(route).set(owner.orNull)

    // Update player state properties
    for id <- PlayerId.all do
      val pps = publicGameState.playerState(id)
      _ticketCount(id).set(pps.ticketCount)
      _cardCount(id).set(pps.cardCount)
      _carCount(id).set(pps.carCount)
      _claimPoints(id).set(pps.claimPoints)

    // Update own tickets
    _tickets.setAll(playerState.tickets.toList.asJava)

    // Update own card counts
    for card <- Card.all do
      _cardCountByType(card).set(playerState.cards.countOf(card))

    // Update can claim route
    for route <- ChMap.routes do
      _canClaimRoute(route).set(canClaimRouteCheck(route))

  private def canClaimRouteCheck(route: Route): Boolean =
    if _publicGameState == null || _playerState == null then false
    else if _publicGameState.claimedRoutes.contains(route) then false
    else if _publicGameState.currentPlayerId != playerId then false
    else _playerState.canClaimRoute(route)

  // Property getters
  def ticketPercentageProperty: ReadOnlyIntegerProperty = _ticketPercentage
  def cardPercentageProperty: ReadOnlyIntegerProperty = _cardPercentage
  def faceUpCardProperty(slot: Int): ReadOnlyObjectProperty[Card] = _faceUpCards(slot)
  def routeOwnerProperty(route: Route): ReadOnlyObjectProperty[PlayerId] = _routeOwner(route)
  def ticketCountProperty(id: PlayerId): ReadOnlyIntegerProperty = _ticketCount(id)
  def cardCountProperty(id: PlayerId): ReadOnlyIntegerProperty = _cardCount(id)
  def carCountProperty(id: PlayerId): ReadOnlyIntegerProperty = _carCount(id)
  def claimPointsProperty(id: PlayerId): ReadOnlyIntegerProperty = _claimPoints(id)
  def ticketsProperty: ObservableList[Ticket] = FXCollections.unmodifiableObservableList(_tickets)
  def cardCountProperty(card: Card): ReadOnlyIntegerProperty = _cardCountByType(card)
  def canClaimRouteProperty(route: Route): ReadOnlyBooleanProperty = _canClaimRoute(route)

  // Convenience getters
  def canDrawTickets: Boolean = _publicGameState != null && _publicGameState.canDrawTickets
  def canDrawCards: Boolean = _publicGameState != null && _publicGameState.canDrawCards

  def possibleClaimCards(route: Route): List[SortedBag[Card]] =
    if _playerState == null then List.empty
    else _playerState.possibleClaimCards(route)
