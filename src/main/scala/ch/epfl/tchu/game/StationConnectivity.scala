package ch.epfl.tchu.game

/** Interface representing the connectivity of a player's network.
  * Determines whether two stations are connected by the player's routes.
  */
trait StationConnectivity:
  /** Checks if two stations are connected by the player's network. */
  def connected(s1: Station, s2: Station): Boolean
