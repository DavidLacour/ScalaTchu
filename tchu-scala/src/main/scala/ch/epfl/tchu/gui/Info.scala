package ch.epfl.tchu.gui

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.{Card, Color, Route, Trail}

/** Generates information messages about game events.
  *
  * @param playerName the name of the player
  */
final class Info(val playerName: String):

  def willPlayFirst(): String = Strings.willPlayFirst.format(playerName)

  def keptTickets(count: Int): String =
    Strings.keptNTickets.format(playerName, count, Strings.plural(count))

  def canPlay(): String = Strings.canPlay.format(playerName)

  def drewTickets(count: Int): String =
    Strings.drewTickets.format(playerName, count, Strings.plural(count))

  def drewBlindCard(): String = Strings.drewBlindCard.format(playerName)

  def drewVisibleCard(card: Card): String =
    Strings.drewVisibleCard.format(playerName, Info.cardName(card, 1))

  def claimedRoute(route: Route, cards: SortedBag[Card]): String =
    Strings.claimedRoute.format(playerName, Info.routeName(route), Info.cardsDescription(cards))

  def attemptsTunnelClaim(route: Route, initialCards: SortedBag[Card]): String =
    Strings.attemptsTunnelClaim.format(playerName, Info.routeName(route), Info.cardsDescription(initialCards))

  def drewAdditionalCards(drawnCards: SortedBag[Card], additionalCost: Int): String =
    val base = Strings.additionalCardsAre.format(Info.cardsDescription(drawnCards))
    if additionalCost == 0 then
      base + Strings.noAdditionalCost
    else
      base + Strings.someAdditionalCost.format(additionalCost, Strings.plural(additionalCost))

  def didNotClaimRoute(route: Route): String =
    Strings.didNotClaimRoute.format(playerName, Info.routeName(route))

  def lastTurnBegins(carCount: Int): String =
    Strings.lastTurnBegins.format(playerName, carCount, Strings.plural(carCount))

  def getsLongestTrailBonus(longestTrail: Trail): String =
    val trailName = longestTrail.station1.get.name + Strings.enDashSeparator + longestTrail.station2.get.name
    Strings.getsBonus.format(playerName, trailName)

  def won(points: Int, loserPoints: Int): String =
    Strings.wins.format(playerName, points, Strings.plural(points), loserPoints, Strings.plural(loserPoints))

object Info:
  /** Returns the name of the given card, properly pluralized. */
  def cardName(card: Card, count: Int): String =
    val name = card match
      case Card.Black => Strings.blackCard
      case Card.Violet => Strings.violetCard
      case Card.Blue => Strings.blueCard
      case Card.Green => Strings.greenCard
      case Card.Yellow => Strings.yellowCard
      case Card.Orange => Strings.orangeCard
      case Card.Red => Strings.redCard
      case Card.White => Strings.whiteCard
      case Card.Locomotive => Strings.locomotiveCard
    name + Strings.plural(count)

  /** Returns a message declaring the given players tied with the given points. */
  def draw(playerNames: List[String], points: Int): String =
    Strings.draw.format(playerNames.mkString(Strings.andSeparator), points)

  private def routeName(route: Route): String =
    route.station1.name + Strings.enDashSeparator + route.station2.name

  private def cardsDescription(cards: SortedBag[Card]): String =
    val parts = cards.toSet.toList.map { card =>
      val count = cards.countOf(card)
      s"$count ${cardName(card, count)}"
    }

    parts match
      case Nil => ""
      case single :: Nil => single
      case _ =>
        val allButLast = parts.init.mkString(", ")
        allButLast + Strings.andSeparator + parts.last
