package ch.epfl.tchu.game

/** Enum representing the eight colors used in the game for wagon cards and routes. */
enum Color:
  case Black, Violet, Blue, Green, Yellow, Orange, Red, White

object Color:
  /** List containing all values of this enum in declaration order. */
  val all: List[Color] = Color.values.toList

  /** The number of color values. */
  val count: Int = all.size
