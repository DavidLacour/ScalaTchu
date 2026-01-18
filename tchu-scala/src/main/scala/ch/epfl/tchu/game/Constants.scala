package ch.epfl.tchu.game

import ch.epfl.tchu.SortedBag

/** Game constants. */
object Constants:
  /** Number of wagon cards of each color. */
  val CarCardsCount: Int = 12

  /** Number of locomotive cards. */
  val LocomotiveCardsCount: Int = 14

  /** Total number of wagon/locomotive cards. */
  val TotalCardsCount: Int = LocomotiveCardsCount + CarCardsCount * Color.count

  /** Set of all cards (110 total). */
  lazy val AllCards: SortedBag[Card] = computeAllCards()

  private def computeAllCards(): SortedBag[Card] =
    val builder = SortedBag.Builder[Card]()
    builder.add(LocomotiveCardsCount, Card.Locomotive)
    Card.cars.foreach(card => builder.add(CarCardsCount, card))
    assert(builder.size == TotalCardsCount)
    builder.build()

  /** Fictitious slot number designating the card deck. */
  val DeckSlot: Int = -1

  /** List of all face-up card slot numbers. */
  val FaceUpCardSlots: List[Int] = List(0, 1, 2, 3, 4)

  /** Number of slots for face-up cards. */
  val FaceUpCardsCount: Int = FaceUpCardSlots.size

  /** Number of tickets distributed to each player at the start of the game. */
  val InitialTicketsCount: Int = 5

  /** Number of cards distributed to each player at the start of the game. */
  val InitialCardsCount: Int = 4

  /** Number of cars each player has at the start of the game. */
  val InitialCarCount: Int = 40

  /** Number of tickets drawn at once during the game. */
  val InGameTicketsCount: Int = 3

  /** Maximum number of tickets a player can discard when drawing. */
  val DiscardableTicketsCount: Int = 2

  /** Number of cards to draw when building a tunnel. */
  val AdditionalTunnelCards: Int = 3

  /** Points obtained for claiming routes of length 1 to 6.
    * Index 0 has an invalid value since routes of length 0 don't exist.
    */
  val RouteClaimPoints: List[Int] = List(Int.MinValue, 1, 2, 4, 7, 10, 15)

  /** Minimum length of a route. */
  val MinRouteLength: Int = 1

  /** Maximum length of a route. */
  val MaxRouteLength: Int = RouteClaimPoints.size - 1

  /** Bonus points for the longest trail. */
  val LongestTrailBonusPoints: Int = 10
