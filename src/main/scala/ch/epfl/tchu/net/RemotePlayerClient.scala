package ch.epfl.tchu.net

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.*
import java.io.*
import java.net.Socket
import java.nio.charset.StandardCharsets.US_ASCII
import scala.util.matching.Regex

/** Client that receives messages from the server and calls the local player. */
final class RemotePlayerClient(player: Player, hostname: String, port: Int):
  private val socket = new Socket(hostname, port)
  private val reader = new BufferedReader(new InputStreamReader(socket.getInputStream, US_ASCII))
  private val writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream, US_ASCII))

  /** Runs the client, processing messages from the server. */
  def run(): Unit =
    var line = reader.readLine()
    while line != null do
      // Split into at most 2 parts: message ID and content
      val parts = line.split(Regex.quote(" "), 2)
      val messageId = MessageId.valueOf(parts(0))
      val content = if parts.length > 1 then parts(1) else ""

      val response = processMessage(messageId, content)
      response.foreach { r =>
        writer.write(r)
        writer.write('\n')
        writer.flush()
      }
      line = reader.readLine()

  private def processMessage(messageId: MessageId, content: String): Option[String] =
    messageId match
      case MessageId.InitPlayers =>
        val parts = content.split(Regex.quote(" "), -1)
        val ownId = Serdes.playerId.deserialize(parts(0))
        val names = Serdes.stringList.deserialize(parts(1))
        val playerNames = Map(
          PlayerId.Player1 -> names(0),
          PlayerId.Player2 -> names(1)
        )
        player.initPlayers(ownId, playerNames)
        None

      case MessageId.ReceiveInfo =>
        val info = Serdes.string.deserialize(content)
        player.receiveInfo(info)
        None

      case MessageId.UpdateState =>
        val parts = content.split(Regex.quote(" "), -1)
        val publicGameState = Serdes.publicGameState.deserialize(parts(0))
        val playerState = Serdes.playerState.deserialize(parts(1))
        player.updateState(publicGameState, playerState)
        None

      case MessageId.SetInitialTickets =>
        val tickets = Serdes.ticketBag.deserialize(content)
        player.setInitialTicketChoice(tickets)
        None

      case MessageId.ChooseInitialTickets =>
        val chosen = player.chooseInitialTickets()
        Some(Serdes.ticketBag.serialize(chosen))

      case MessageId.NextTurn =>
        val turnKind = player.nextTurn()
        Some(Serdes.turnKind.serialize(turnKind))

      case MessageId.ChooseTickets =>
        val options = Serdes.ticketBag.deserialize(content)
        val chosen = player.chooseTickets(options)
        Some(Serdes.ticketBag.serialize(chosen))

      case MessageId.DrawSlot =>
        val slot = player.drawSlot()
        Some(Serdes.integer.serialize(slot))

      case MessageId.Route =>
        val route = player.claimedRoute()
        Some(Serdes.route.serialize(route))

      case MessageId.Cards =>
        val cards = player.initialClaimCards()
        Some(Serdes.cardBag.serialize(cards))

      case MessageId.ChooseAdditionalCards =>
        val options = Serdes.cardBagList.deserialize(content)
        val chosen = player.chooseAdditionalCards(options)
        Some(Serdes.cardBag.serialize(chosen))
