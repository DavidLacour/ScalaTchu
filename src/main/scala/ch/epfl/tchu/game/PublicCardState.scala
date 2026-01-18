package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument

/** Represents the public (visible) part of the card state.
  * This includes face-up cards, deck size, and discard pile size.
  *
  * @param faceUpCards the 5 face-up cards
  * @param deckSize the number of cards in the deck
  * @param discardsSize the number of cards in the discard pile
  */
class PublicCardState(
    val faceUpCards: List[Card],
    val deckSize: Int,
    val discardsSize: Int
):
  checkArgument(faceUpCards.size == Constants.FaceUpCardsCount,
    s"must have exactly ${Constants.FaceUpCardsCount} face-up cards")
  checkArgument(deckSize >= 0, "deck size must be non-negative")
  checkArgument(discardsSize >= 0, "discards size must be non-negative")

  /** Returns the total number of cards not in players' hands. */
  def totalSize: Int = faceUpCards.size + deckSize + discardsSize

  /** Returns the face-up card at the given index (0-4). */
  def faceUpCard(slot: Int): Card =
    checkArgument(slot >= 0 && slot < Constants.FaceUpCardsCount,
      s"slot must be between 0 and ${Constants.FaceUpCardsCount - 1}")
    faceUpCards(slot)

  /** Returns true if the deck is empty. */
  def isDeckEmpty: Boolean = deckSize == 0
