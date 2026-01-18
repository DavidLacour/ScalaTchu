package ch.epfl.tchu.net

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.*
import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.matching.Regex

/** Contains predefined serdes for game types. */
object Serdes:
  // Separators
  private val ElementSeparator = ","
  private val ComponentSeparator = ";"
  private val StructureSeparator = ":"

  // Basic type serdes
  val integer: Serde[Int] = Serde.of(_.toString, _.toInt)

  val string: Serde[String] = Serde.of(
    s => Base64.getEncoder.encodeToString(s.getBytes(StandardCharsets.UTF_8)),
    s => new String(Base64.getDecoder.decode(s), StandardCharsets.UTF_8)
  )

  // Enum serdes
  val playerId: Serde[PlayerId] = Serde.oneOf(PlayerId.all)
  val turnKind: Serde[TurnKind] = Serde.oneOf(TurnKind.all)
  val card: Serde[Card] = Serde.oneOf(Card.all)
  val route: Serde[Route] = Serde.oneOf(ChMap.routes)
  val ticket: Serde[Ticket] = Serde.oneOf(ChMap.tickets)

  // List serdes
  val stringList: Serde[List[String]] = Serde.listOf(string, ElementSeparator)
  val cardList: Serde[List[Card]] = Serde.listOf(card, ElementSeparator)
  val routeList: Serde[List[Route]] = Serde.listOf(route, ElementSeparator)

  // Bag serdes
  val cardBag: Serde[SortedBag[Card]] = Serde.bagOf(card, ElementSeparator)
  val ticketBag: Serde[SortedBag[Ticket]] = Serde.bagOf(ticket, ElementSeparator)
  val cardBagList: Serde[List[SortedBag[Card]]] = Serde.listOf(cardBag, ComponentSeparator)

  // Composite serdes
  val publicCardState: Serde[PublicCardState] = new Serde[PublicCardState]:
    def serialize(pcs: PublicCardState): String =
      List(
        cardList.serialize(pcs.faceUpCards),
        integer.serialize(pcs.deckSize),
        integer.serialize(pcs.discardsSize)
      ).mkString(ComponentSeparator)

    def deserialize(str: String): PublicCardState =
      val parts = str.split(Regex.quote(ComponentSeparator), -1)
      PublicCardState(
        cardList.deserialize(parts(0)),
        integer.deserialize(parts(1)),
        integer.deserialize(parts(2))
      )

  val publicPlayerState: Serde[PublicPlayerState] = new Serde[PublicPlayerState]:
    def serialize(pps: PublicPlayerState): String =
      List(
        integer.serialize(pps.ticketCount),
        integer.serialize(pps.cardCount),
        routeList.serialize(pps.routes)
      ).mkString(ComponentSeparator)

    def deserialize(str: String): PublicPlayerState =
      val parts = str.split(Regex.quote(ComponentSeparator), -1)
      PublicPlayerState(
        integer.deserialize(parts(0)),
        integer.deserialize(parts(1)),
        routeList.deserialize(parts(2))
      )

  val playerState: Serde[PlayerState] = new Serde[PlayerState]:
    def serialize(ps: PlayerState): String =
      List(
        ticketBag.serialize(ps.tickets),
        cardBag.serialize(ps.cards),
        routeList.serialize(ps.routes)
      ).mkString(ComponentSeparator)

    def deserialize(str: String): PlayerState =
      val parts = str.split(Regex.quote(ComponentSeparator), -1)
      PlayerState(
        ticketBag.deserialize(parts(0)),
        cardBag.deserialize(parts(1)),
        routeList.deserialize(parts(2))
      )

  val publicGameState: Serde[PublicGameState] = new Serde[PublicGameState]:
    def serialize(pgs: PublicGameState): String =
      val lastPlayerStr = pgs.lastPlayer.map(playerId.serialize).getOrElse("")
      List(
        integer.serialize(pgs.ticketsCount),
        publicCardState.serialize(pgs.cardState),
        playerId.serialize(pgs.currentPlayerId),
        publicPlayerState.serialize(pgs.playerState(PlayerId.Player1)),
        publicPlayerState.serialize(pgs.playerState(PlayerId.Player2)),
        lastPlayerStr
      ).mkString(StructureSeparator)

    def deserialize(str: String): PublicGameState =
      val parts = str.split(Regex.quote(StructureSeparator), -1)
      val lastPlayer = if parts(5).isEmpty then None else Some(playerId.deserialize(parts(5)))
      PublicGameState(
        integer.deserialize(parts(0)),
        publicCardState.deserialize(parts(1)),
        playerId.deserialize(parts(2)),
        Map(
          PlayerId.Player1 -> publicPlayerState.deserialize(parts(3)),
          PlayerId.Player2 -> publicPlayerState.deserialize(parts(4))
        ),
        lastPlayer
      )
