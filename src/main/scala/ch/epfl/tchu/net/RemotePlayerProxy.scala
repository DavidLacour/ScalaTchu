package ch.epfl.tchu.net

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.*
import java.io.*
import java.net.Socket
import java.nio.charset.StandardCharsets.US_ASCII

/** Proxy that represents a remote player.
  * Sends messages to the remote client and receives responses.
  */
final class RemotePlayerProxy(socket: Socket) extends Player:
  private val reader = new BufferedReader(new InputStreamReader(socket.getInputStream, US_ASCII))
  private val writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream, US_ASCII))

  override def initPlayers(ownId: PlayerId, playerNames: Map[PlayerId, String]): Unit =
    val names = List(playerNames(PlayerId.Player1), playerNames(PlayerId.Player2))
    val content = s"${Serdes.playerId.serialize(ownId)} ${Serdes.stringList.serialize(names)}"
    sendMessage(MessageId.InitPlayers, content)

  override def receiveInfo(info: String): Unit =
    sendMessage(MessageId.ReceiveInfo, Serdes.string.serialize(info))

  override def updateState(newState: PublicGameState, ownState: PlayerState): Unit =
    val content = s"${Serdes.publicGameState.serialize(newState)} ${Serdes.playerState.serialize(ownState)}"
    sendMessage(MessageId.UpdateState, content)

  override def setInitialTicketChoice(tickets: SortedBag[Ticket]): Unit =
    sendMessage(MessageId.SetInitialTickets, Serdes.ticketBag.serialize(tickets))

  override def chooseInitialTickets(): SortedBag[Ticket] =
    sendMessage(MessageId.ChooseInitialTickets, "")
    Serdes.ticketBag.deserialize(receiveMessage())

  override def nextTurn(): TurnKind =
    sendMessage(MessageId.NextTurn, "")
    Serdes.turnKind.deserialize(receiveMessage())

  override def chooseTickets(options: SortedBag[Ticket]): SortedBag[Ticket] =
    sendMessage(MessageId.ChooseTickets, Serdes.ticketBag.serialize(options))
    Serdes.ticketBag.deserialize(receiveMessage())

  override def drawSlot(): Int =
    sendMessage(MessageId.DrawSlot, "")
    Serdes.integer.deserialize(receiveMessage())

  override def claimedRoute(): Route =
    sendMessage(MessageId.Route, "")
    Serdes.route.deserialize(receiveMessage())

  override def initialClaimCards(): SortedBag[Card] =
    sendMessage(MessageId.Cards, "")
    Serdes.cardBag.deserialize(receiveMessage())

  override def chooseAdditionalCards(options: List[SortedBag[Card]]): SortedBag[Card] =
    sendMessage(MessageId.ChooseAdditionalCards, Serdes.cardBagList.serialize(options))
    Serdes.cardBag.deserialize(receiveMessage())

  private def sendMessage(id: MessageId, content: String): Unit =
    writer.write(s"${id.toString} $content")
    writer.write('\n')
    writer.flush()

  private def receiveMessage(): String =
    reader.readLine()
