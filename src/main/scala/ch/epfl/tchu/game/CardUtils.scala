package ch.epfl.tchu.game

import ch.epfl.tchu.SortedBag

/** Utility methods for working with cards. */
object CardUtils:
  /** Extracts the wagon color from a set of cards (ignoring locomotives).
    * Returns None if only locomotives are present.
    */
  def wagonColor(cards: SortedBag[Card]): Option[Color] =
    cards.iterator.find(_ != Card.Locomotive).flatMap(_.color)
