package ch.epfl.tchu.gui

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.*
import javafx.application.Platform
import scala.compiletime.uninitialized

import java.util.concurrent.{ArrayBlockingQueue, CountDownLatch}

/** Adapter that wraps a GraphicalPlayer and implements the Player trait. */
final class GraphicalPlayerAdapter extends Player:

  private var graphicalPlayer: GraphicalPlayer = uninitialized
  private val ticketQueue = new ArrayBlockingQueue[SortedBag[Ticket]](1)
  private val turnKindQueue = new ArrayBlockingQueue[TurnKind](1)
  private val cardSlotQueue = new ArrayBlockingQueue[Integer](1)
  private val routeQueue = new ArrayBlockingQueue[Route](1)
  private val cardQueue = new ArrayBlockingQueue[SortedBag[Card]](1)

  override def initPlayers(ownId: PlayerId, playerNames: Map[PlayerId, String]): Unit =
    println(s"initPlayers called for $ownId")
    val latch = new CountDownLatch(1)
    Platform.runLater { () =>
      try
        println(s"Creating GraphicalPlayer for $ownId...")
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames)
        println(s"GraphicalPlayer created for $ownId")
      catch
        case e: Exception =>
          println(s"ERROR creating GraphicalPlayer: ${e.getMessage}")
          e.printStackTrace()
      finally
        latch.countDown()
    }
    latch.await() // Wait for GUI to be created
    println(s"initPlayers done for $ownId")

  override def receiveInfo(info: String): Unit =
    Platform.runLater(() => graphicalPlayer.receiveInfo(info))

  override def updateState(newState: PublicGameState, ownState: PlayerState): Unit =
    Platform.runLater(() => graphicalPlayer.setState(newState, ownState))

  override def setInitialTicketChoice(tickets: SortedBag[Ticket]): Unit =
    Platform.runLater { () =>
      graphicalPlayer.chooseTickets(tickets, chosen => {
        try ticketQueue.put(chosen)
        catch case _: InterruptedException => Thread.currentThread.interrupt()
      })
    }

  override def chooseInitialTickets(): SortedBag[Ticket] =
    try ticketQueue.take()
    catch
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw new Error(e)

  override def nextTurn(): TurnKind =
    Platform.runLater { () =>
      graphicalPlayer.startTurn(
        () => putTurnKind(TurnKind.DrawTickets),
        slot => {
          putTurnKind(TurnKind.DrawCards)
          putCardSlot(slot)
        },
        (route, cards) => {
          putTurnKind(TurnKind.ClaimRoute)
          putRoute(route)
          putCards(cards)
        }
      )
    }

    try turnKindQueue.take()
    catch
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw new Error(e)

  override def chooseTickets(options: SortedBag[Ticket]): SortedBag[Ticket] =
    Platform.runLater { () =>
      graphicalPlayer.chooseTickets(options, chosen => {
        try ticketQueue.put(chosen)
        catch case _: InterruptedException => Thread.currentThread.interrupt()
      })
    }

    try ticketQueue.take()
    catch
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw new Error(e)

  override def drawSlot(): Int =
    // First call returns the slot from startTurn
    val slot = cardSlotQueue.poll()
    if slot != null then return slot

    // Second call waits for user to choose
    Platform.runLater(() => graphicalPlayer.drawCard(putCardSlot))

    try cardSlotQueue.take()
    catch
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw new Error(e)

  override def claimedRoute(): Route =
    try routeQueue.take()
    catch
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw new Error(e)

  override def initialClaimCards(): SortedBag[Card] =
    try cardQueue.take()
    catch
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw new Error(e)

  override def chooseAdditionalCards(options: List[SortedBag[Card]]): SortedBag[Card] =
    Platform.runLater(() => graphicalPlayer.chooseAdditionalCards(options, putCards))

    try cardQueue.take()
    catch
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw new Error(e)

  private def putTurnKind(turnKind: TurnKind): Unit =
    try turnKindQueue.put(turnKind)
    catch case _: InterruptedException => Thread.currentThread.interrupt()

  private def putCardSlot(slot: Int): Unit =
    try cardSlotQueue.put(slot)
    catch case _: InterruptedException => Thread.currentThread.interrupt()

  private def putRoute(route: Route): Unit =
    try routeQueue.put(route)
    catch case _: InterruptedException => Thread.currentThread.interrupt()

  private def putCards(cards: SortedBag[Card]): Unit =
    try cardQueue.put(cards)
    catch case _: InterruptedException => Thread.currentThread.interrupt()
