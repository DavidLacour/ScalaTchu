package ch.epfl.tchu.gui

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.{Card, Route, Ticket}

/** Functional interfaces for handling player actions in the GUI. */
object ActionHandlers:

  /** Handler for drawing tickets. */
  trait DrawTicketsHandler:
    def onDrawTickets(): Unit

  /** Handler for drawing cards. */
  trait DrawCardHandler:
    def onDrawCard(slot: Int): Unit

  /** Handler for claiming routes. */
  trait ClaimRouteHandler:
    def onClaimRoute(route: Route, cards: SortedBag[Card]): Unit

  /** Handler for choosing tickets. */
  trait ChooseTicketsHandler:
    def onChooseTickets(tickets: SortedBag[Ticket]): Unit

  /** Handler for choosing cards. */
  trait ChooseCardsHandler:
    def onChooseCards(cards: SortedBag[Card]): Unit
