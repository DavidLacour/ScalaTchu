package ch.epfl.tchu.gui

import ch.epfl.tchu.game.PlayerId
import javafx.beans.binding.Bindings
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.Separator
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import javafx.scene.text.{Text, TextFlow}

/** Creates the info view (player stats and game messages). */
object InfoViewCreator:

  /** Creates the info view. */
  def createInfoView(
      playerId: PlayerId,
      playerNames: Map[PlayerId, String],
      gameState: ObservableGameState,
      gameMessages: ObservableList[Text]
  ): Node =
    val infoView = new VBox()
    infoView.getStylesheets.add("info.css")

    // Player stats
    val playerStats = new VBox()
    playerStats.setId("player-stats")

    for id <- PlayerId.all do
      val playerInfo = createPlayerInfo(id, playerNames(id), gameState)
      playerStats.getChildren.add(playerInfo)

    // Separator
    val separator = new Separator()

    // Game messages
    val messagesFlow = new TextFlow()
    messagesFlow.setId("game-info")
    Bindings.bindContent(messagesFlow.getChildren, gameMessages)

    infoView.getChildren.addAll(playerStats, separator, messagesFlow)
    infoView

  private def createPlayerInfo(id: PlayerId, name: String, gameState: ObservableGameState): TextFlow =
    val textFlow = new TextFlow()
    textFlow.getStyleClass.add(id.toString)

    val circle = new Circle(5)
    circle.getStyleClass.add("filled")

    val text = new Text()
    text.textProperty.bind(Bindings.format(
      Strings.playerStats,
      name,
      gameState.ticketCountProperty(id),
      gameState.cardCountProperty(id),
      gameState.carCountProperty(id),
      gameState.claimPointsProperty(id)
    ))

    textFlow.getChildren.addAll(circle, text)
    textFlow
