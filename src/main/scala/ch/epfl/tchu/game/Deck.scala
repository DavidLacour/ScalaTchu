package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument
import ch.epfl.tchu.SortedBag
import scala.util.Random

/** Represents an immutable deck (pile) of cards.
  * Cards can be drawn from the top of the deck.
  */
final class Deck[C: Ordering] private (private val cards: List[C]):
  /** Returns the number of cards in this deck. */
  def size: Int = cards.size

  /** Returns true if this deck is empty. */
  def isEmpty: Boolean = cards.isEmpty

  /** Returns the card at the top of this deck. */
  def topCard: C =
    checkArgument(!isEmpty, "deck is empty")
    cards.head

  /** Returns a new deck identical to this one but without the top card. */
  def withoutTopCard: Deck[C] =
    checkArgument(!isEmpty, "deck is empty")
    new Deck(cards.tail)

  /** Returns a sorted bag containing the top count cards. */
  def topCards(count: Int): SortedBag[C] =
    checkArgument(count >= 0 && count <= size, s"invalid count: $count")
    SortedBag.of(cards.take(count))

  /** Returns a new deck identical to this one but without the top count cards. */
  def withoutTopCards(count: Int): Deck[C] =
    checkArgument(count >= 0 && count <= size, s"invalid count: $count")
    new Deck(cards.drop(count))

object Deck:
  /** Creates a shuffled deck from the given cards. */
  def of[C: Ordering](cards: SortedBag[C], rng: Random): Deck[C] =
    new Deck(rng.shuffle(cards.toList))
