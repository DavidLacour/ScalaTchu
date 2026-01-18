package ch.epfl.tchu.game

/** Enum representing the identity of a player. tCHu is a two-player game. */
enum PlayerId:
  case Player1, Player2

  /** Returns the identity of the next player. */
  def next: PlayerId = this match
    case Player1 => Player2
    case Player2 => Player1

object PlayerId:
  /** List containing all values of this enum in declaration order. */
  val all: List[PlayerId] = PlayerId.values.toList

  /** The number of players. */
  val count: Int = all.size
