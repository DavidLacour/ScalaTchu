package ch.epfl.tchu.game

/** Enum representing the different types of cards in the game:
  * eight types of wagon cards (one per color) and the locomotive card type.
  */
enum Card(val color: Option[Color]):
  case Black extends Card(Some(Color.Black))
  case Violet extends Card(Some(Color.Violet))
  case Blue extends Card(Some(Color.Blue))
  case Green extends Card(Some(Color.Green))
  case Yellow extends Card(Some(Color.Yellow))
  case Orange extends Card(Some(Color.Orange))
  case Red extends Card(Some(Color.Red))
  case White extends Card(Some(Color.White))
  case Locomotive extends Card(None)

object Card:
  /** List containing all values of this enum in declaration order. */
  val all: List[Card] = Card.values.toList

  /** The number of card types. */
  val count: Int = all.size

  /** List containing only the wagon cards (not locomotive), in declaration order. */
  val cars: List[Card] = List(Black, Violet, Blue, Green, Yellow, Orange, Red, White)

  /** Returns the wagon card type corresponding to the given color. */
  def of(color: Color): Card = cars(color.ordinal)

  /** Ordering for Card based on ordinal value. */
  given Ordering[Card] = Ordering.by(_.ordinal)
