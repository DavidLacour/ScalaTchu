package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument

/** Represents a station (railway station) in the game network.
  * A station has a unique identification number and a name.
  *
  * @param id the unique identification number (must be non-negative)
  * @param name the name of the station
  */
final case class Station(id: Int, name: String):
  checkArgument(id >= 0, s"Station id must be non-negative, got $id")

  override def toString: String = name
