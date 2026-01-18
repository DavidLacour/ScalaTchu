package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument

/** Represents a flattened partition of stations.
  * Used to efficiently determine if two stations are connected.
  */
final class StationPartition private (private val representatives: Array[Int]) extends StationConnectivity:
  /** Checks if two stations are connected in this partition.
    * Stations outside the partition bounds are only connected to themselves.
    */
  def connected(s1: Station, s2: Station): Boolean =
    val id1 = s1.id
    val id2 = s2.id

    // If either station is out of bounds, they're only connected if they're the same
    if id1 >= representatives.length || id2 >= representatives.length then
      id1 == id2
    else
      representatives(id1) == representatives(id2)

object StationPartition:
  /** Builder for constructing StationPartition instances. */
  class Builder(stationCount: Int):
    checkArgument(stationCount >= 0, "stationCount must be non-negative")

    private val links = Array.tabulate(stationCount)(identity)

    /** Connects two stations, joining their subsets. */
    def connect(s1: Station, s2: Station): this.type =
      val rep1 = representative(s1.id)
      val rep2 = representative(s2.id)
      links(rep1) = rep2
      this

    /** Builds and returns the flattened partition. */
    def build(): StationPartition =
      val flattened = Array.tabulate(links.length)(representative)
      new StationPartition(flattened)

    /** Finds the representative of the subset containing the given station. */
    private def representative(stationId: Int): Int =
      var current = stationId
      while links(current) != current do
        current = links(current)
      current
