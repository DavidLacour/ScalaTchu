package ch.epfl.tchu.gui

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.Card
import javafx.util.StringConverter

/** Converter between SortedBag[Card] and String for GUI display. */
class CardBagStringConverter extends StringConverter[SortedBag[Card]]:

  override def toString(cards: SortedBag[Card]): String =
    if cards == null || cards.isEmpty then ""
    else
      val parts = Card.all.flatMap { card =>
        val count = cards.countOf(card)
        if count > 0 then Some(s"$count ${cardName(card, count)}")
        else None
      }
      parts.mkString(" + ")

  override def fromString(string: String): SortedBag[Card] =
    throw new UnsupportedOperationException("Cannot convert string to card bag")

  private def cardName(card: Card, count: Int): String =
    val baseName = card match
      case Card.Locomotive => Strings.locomotiveCard
      case Card.Black => Strings.blackCard
      case Card.Violet => Strings.violetCard
      case Card.Blue => Strings.blueCard
      case Card.Green => Strings.greenCard
      case Card.Yellow => Strings.yellowCard
      case Card.Orange => Strings.orangeCard
      case Card.Red => Strings.redCard
      case Card.White => Strings.whiteCard
    if count > 1 then baseName + "s" else baseName
