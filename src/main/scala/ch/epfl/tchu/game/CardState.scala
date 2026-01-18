package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument
import ch.epfl.tchu.SortedBag
import scala.util.Random

/** Represents the complete state of cards not in players' hands.
  * Extends PublicCardState with the private parts (deck and discards contents).
  */
final class CardState private (
    faceUpCards: List[Card],
    private val deck: Deck[Card],
    private val discards: SortedBag[Card]
) extends PublicCardState(faceUpCards, deck.size, discards.size):

  /** Returns a new CardState with the face-up card at slot replaced by the top deck card. */
  def withDrawnFaceUpCard(slot: Int): CardState =
    require(slot >= 0 && slot < Constants.FaceUpCardsCount,
      s"slot must be between 0 and ${Constants.FaceUpCardsCount - 1}")
    checkArgument(!deck.isEmpty, "deck is empty")

    val newFaceUpCards = faceUpCards.updated(slot, deck.topCard)
    new CardState(newFaceUpCards, deck.withoutTopCard, discards)

  /** Returns the card at the top of the deck. */
  def topDeckCard: Card =
    checkArgument(!deck.isEmpty, "deck is empty")
    deck.topCard

  /** Returns a new CardState without the top deck card. */
  def withoutTopDeckCard: CardState =
    checkArgument(!deck.isEmpty, "deck is empty")
    new CardState(faceUpCards, deck.withoutTopCard, discards)

  /** Returns a new CardState with the deck recreated from the shuffled discards. */
  def withDeckRecreatedFromDiscards(rng: Random): CardState =
    checkArgument(deck.isEmpty, "deck is not empty")
    val newDeck = Deck.of(discards, rng)
    new CardState(faceUpCards, newDeck, SortedBag.of[Card])

  /** Returns a new CardState with additional cards added to the discards. */
  def withMoreDiscardedCards(additionalDiscards: SortedBag[Card]): CardState =
    new CardState(faceUpCards, deck, discards.union(additionalDiscards))

object CardState:
  /** Creates a CardState from a deck of cards. */
  def of(deck: Deck[Card]): CardState =
    checkArgument(deck.size >= Constants.FaceUpCardsCount,
      s"deck must have at least ${Constants.FaceUpCardsCount} cards")

    var currentDeck = deck
    val faceUpCards = (0 until Constants.FaceUpCardsCount).map { _ =>
      val card = currentDeck.topCard
      currentDeck = currentDeck.withoutTopCard
      card
    }.toList

    new CardState(faceUpCards, currentDeck, SortedBag.of[Card])
