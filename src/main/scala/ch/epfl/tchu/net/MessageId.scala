package ch.epfl.tchu.net

/** Enum representing the types of messages exchanged between client and server. */
enum MessageId:
  case InitPlayers, ReceiveInfo, UpdateState, SetInitialTickets,
       ChooseInitialTickets, NextTurn, ChooseTickets, DrawSlot,
       Route, Cards, ChooseAdditionalCards

object MessageId:
  /** List containing all values of this enum. */
  val all: List[MessageId] = MessageId.values.toList
