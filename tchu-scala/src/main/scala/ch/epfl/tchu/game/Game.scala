package ch.epfl.tchu.game

import ch.epfl.tchu.Preconditions.checkArgument
import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.gui.Info
import scala.util.Random

/** Represents the main game logic and orchestrates gameplay. */
object Game:

  /** Plays a game of tCHu with the given players, tickets, and random generator. */
  def play(
      players: Map[PlayerId, Player],
      playerNames: Map[PlayerId, String],
      tickets: SortedBag[Ticket],
      rng: Random
  ): Unit =
    checkArgument(players.size == PlayerId.count, s"must have ${PlayerId.count} players")
    checkArgument(playerNames.size == PlayerId.count, s"must have ${PlayerId.count} player names")

    // Create Info objects for each player
    val infos = PlayerId.all.map(id => id -> Info(playerNames(id))).toMap

    // Initialize players
    PlayerId.all.foreach(id => players(id).initPlayers(id, playerNames))

    // Create initial game state
    var gameState = GameState.initial(tickets, rng)

    // Inform players of first player
    sendInfo(players, infos(gameState.currentPlayerId).willPlayFirst())

    // Initial ticket choice for each player
    PlayerId.all.foreach { id =>
      players(id).setInitialTicketChoice(gameState.topTickets(Constants.InitialTicketsCount))
      gameState = gameState.withoutTopTickets(Constants.InitialTicketsCount)
    }

    updateState(players, gameState)

    // Players choose initial tickets
    PlayerId.all.foreach { id =>
      val chosenTickets = players(id).chooseInitialTickets()
      gameState = gameState.withInitiallyChosenTickets(id, chosenTickets)
    }

    // Announce how many tickets each player kept
    PlayerId.all.foreach { id =>
      val ticketCount = gameState.playerState(id).ticketCount
      sendInfo(players, infos(id).keptTickets(ticketCount))
    }

    // Main game loop
    var lastTurnAnnounced = false
    var gameOver = false

    while !gameOver do
      val currentPlayer = gameState.currentPlayerId
      val player = players(currentPlayer)
      val info = infos(currentPlayer)

      updateState(players, gameState)
      sendInfo(players, info.canPlay())

      val turnKind = player.nextTurn()

      turnKind match
        case TurnKind.DrawTickets =>
          gameState = handleDrawTickets(players, gameState, player, info)

        case TurnKind.DrawCards =>
          gameState = handleDrawCards(players, gameState, player, info, rng)

        case TurnKind.ClaimRoute =>
          gameState = handleClaimRoute(players, gameState, player, info, rng)

      // Check if last turn begins
      if !lastTurnAnnounced && gameState.lastTurnBegins then
        sendInfo(players, info.lastTurnBegins(gameState.currentPlayerState.carCount))
        lastTurnAnnounced = true

      // Check if game is over
      if gameState.lastPlayer.contains(currentPlayer) then
        gameOver = true
      else
        gameState = gameState.forNextTurn

    // Final update
    updateState(players, gameState)

    // Calculate final scores
    val points = scala.collection.mutable.Map[PlayerId, Int]()
    val longestTrails = scala.collection.mutable.Map[PlayerId, Trail]()

    PlayerId.all.foreach { id =>
      val longest = Trail.longest(gameState.playerState(id).routes)
      longestTrails(id) = longest
      points(id) = gameState.playerState(id).finalPoints
    }

    // Determine longest trail winner(s)
    val maxTrailLength = longestTrails.values.map(_.length).max

    PlayerId.all.foreach { id =>
      val trail = longestTrails(id)
      if trail.length == maxTrailLength then
        sendInfo(players, infos(id).getsLongestTrailBonus(trail))
        points(id) = points(id) + Constants.LongestTrailBonusPoints
    }

    // Announce winner
    val p1Points = points(PlayerId.Player1)
    val p2Points = points(PlayerId.Player2)

    if p1Points > p2Points then
      sendInfo(players, infos(PlayerId.Player1).won(p1Points, p2Points))
    else if p2Points > p1Points then
      sendInfo(players, infos(PlayerId.Player2).won(p2Points, p1Points))
    else
      // Draw
      val names = List(playerNames(PlayerId.Player1), playerNames(PlayerId.Player2))
      sendInfo(players, Info.draw(names, p1Points))

  private def sendInfo(players: Map[PlayerId, Player], info: String): Unit =
    players.values.foreach(_.receiveInfo(info))

  private def updateState(players: Map[PlayerId, Player], gameState: GameState): Unit =
    PlayerId.all.foreach(id => players(id).updateState(gameState, gameState.playerState(id)))

  private def handleDrawTickets(
      players: Map[PlayerId, Player],
      gameState: GameState,
      player: Player,
      info: Info
  ): GameState =
    val drawnTickets = gameState.topTickets(Constants.InGameTicketsCount)
    sendInfo(players, info.drewTickets(Constants.InGameTicketsCount))

    val chosenTickets = player.chooseTickets(drawnTickets)
    sendInfo(players, info.keptTickets(chosenTickets.size))

    gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets)

  private def handleDrawCards(
      players: Map[PlayerId, Player],
      initialGameState: GameState,
      player: Player,
      info: Info,
      rng: Random
  ): GameState =
    var gs = initialGameState
    for i <- 0 until 2 do
      gs = gs.withCardsDeckRecreatedIfNeeded(rng)

      val slot = player.drawSlot()

      if slot == Constants.DeckSlot then
        gs = gs.withBlindlyDrawnCard
        sendInfo(players, info.drewBlindCard())
      else
        val drawnCard = gs.cardState.faceUpCard(slot)
        sendInfo(players, info.drewVisibleCard(drawnCard))
        gs = gs.withDrawnFaceUpCard(slot)

      if i == 0 then
        updateState(players, gs)

    gs

  private def handleClaimRoute(
      players: Map[PlayerId, Player],
      initialGameState: GameState,
      player: Player,
      info: Info,
      rng: Random
  ): GameState =
    val claimedRoute = player.claimedRoute()
    val initialClaimCards = player.initialClaimCards()

    if claimedRoute.level == Level.Overground then
      // Surface route - just claim it
      sendInfo(players, info.claimedRoute(claimedRoute, initialClaimCards))
      initialGameState.withClaimedRoute(claimedRoute, initialClaimCards)
    else
      // Tunnel - draw additional cards
      sendInfo(players, info.attemptsTunnelClaim(claimedRoute, initialClaimCards))

      // Draw 3 cards for tunnel attempt
      var gs = initialGameState
      val drawnCardsBuilder = SortedBag.Builder[Card]()
      for _ <- 0 until Constants.AdditionalTunnelCards do
        gs = gs.withCardsDeckRecreatedIfNeeded(rng)
        drawnCardsBuilder.add(gs.topCard)
        gs = gs.withoutTopCard
      val drawnCards = drawnCardsBuilder.build()

      val additionalCount = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards)
      sendInfo(players, info.drewAdditionalCards(drawnCards, additionalCount))

      if additionalCount == 0 then
        // No additional cards needed
        sendInfo(players, info.claimedRoute(claimedRoute, initialClaimCards))
        gs.withClaimedRoute(claimedRoute, initialClaimCards)
          .withMoreDiscardedCards(drawnCards)
      else
        // Need additional cards
        val possibleAdditional = gs.currentPlayerState
          .possibleAdditionalCards(additionalCount, initialClaimCards, drawnCards)

        if possibleAdditional.isEmpty then
          // Player can't afford additional cards
          sendInfo(players, info.didNotClaimRoute(claimedRoute))
          gs.withMoreDiscardedCards(drawnCards)
        else
          val additionalCards = player.chooseAdditionalCards(possibleAdditional)

          if additionalCards.isEmpty then
            // Player chose not to claim
            sendInfo(players, info.didNotClaimRoute(claimedRoute))
            gs.withMoreDiscardedCards(drawnCards)
          else
            // Claim with additional cards
            val allCards = initialClaimCards.union(additionalCards)
            sendInfo(players, info.claimedRoute(claimedRoute, allCards))
            gs.withClaimedRoute(claimedRoute, allCards)
              .withMoreDiscardedCards(drawnCards)
