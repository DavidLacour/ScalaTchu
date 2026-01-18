package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument
import scala.collection.immutable.TreeSet

/** Represents a ticket (objective) in the game.
  * A ticket consists of one or more trips between stations.
  *
  * @param trips the list of trips constituting this ticket
  */
final class Ticket private (val trips: List[Trip], val text: String) extends Ordered[Ticket]:

  /** Computes the points for this ticket based on the player's network connectivity.
    * Returns the maximum points if at least one trip is connected,
    * or the minimum (most negative) points if no trips are connected.
    */
  def points(connectivity: StationConnectivity): Int =
    trips.map(_.points(connectivity)).max

  /** Compares this ticket to another by alphabetical order of their textual representation. */
  def compare(that: Ticket): Int = this.text.compare(that.text)

  override def toString: String = text

object Ticket:
  /** Ordering for Ticket based on text comparison. */
  given Ordering[Ticket] = Ordering.by(_.text)

  /** Constructs a ticket from a list of trips. */
  def apply(trips: List[Trip]): Ticket =
    checkArgument(trips.nonEmpty, "trips list cannot be empty")

    // Check all departure stations have the same name
    val departureName = trips.head.from.name
    trips.foreach { trip =>
      checkArgument(trip.from.name == departureName, "all departure stations must have the same name")
    }

    new Ticket(trips, computeText(trips))

  /** Constructs a ticket with a single trip. */
  def apply(from: Station, to: Station, points: Int): Ticket =
    apply(List(Trip(from, to, points)))

  private def computeText(trips: List[Trip]): String =
    val departureName = trips.head.from.name

    if trips.size == 1 then
      val trip = trips.head
      s"$departureName - ${trip.to.name} (${trip.points})"
    else
      // Group destinations by name with their points
      val destinations = TreeSet.from(trips.map(t => s"${t.to.name} (${t.points})"))
      val destinationText = destinations.mkString(", ")
      s"$departureName - {$destinationText}"
