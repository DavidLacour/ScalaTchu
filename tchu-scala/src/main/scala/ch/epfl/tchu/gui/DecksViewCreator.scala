package ch.epfl.tchu.gui

import ch.epfl.tchu.game.{Card, Constants, Ticket}
import javafx.beans.binding.Bindings
import javafx.beans.property.{ObjectProperty, ReadOnlyIntegerProperty}
import javafx.scene.{Group, Node}
import javafx.scene.control.{Button, ListView}
import javafx.scene.layout.{HBox, StackPane, VBox}
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text

/** Creates the decks view (card and ticket piles). */
object DecksViewCreator:

  private val CardWidth = 60
  private val CardHeight = 90
  private val DeckOffset = 50

  /** Creates the hand view showing the player's cards and tickets. */
  def createHandView(gameState: ObservableGameState): Node =
    val handView = new HBox()
    handView.getStylesheets.addAll("decks.css", "colors.css")

    // Tickets list
    val ticketsList = new ListView[Ticket](gameState.ticketsProperty)
    ticketsList.setId("tickets")

    // Hand pane with cards
    val handPane = new HBox()
    handPane.setId("hand-pane")

    for card <- Card.all do
      val cardPane = createCardPane(card, gameState.cardCountProperty(card))
      handPane.getChildren.add(cardPane)

    handView.getChildren.addAll(ticketsList, handPane)
    handView

  /** Creates the card chooser view for face-up cards and decks. */
  def createCardsView(
      gameState: ObservableGameState,
      drawTicketsHandler: ObjectProperty[ActionHandlers.DrawTicketsHandler],
      drawCardHandler: ObjectProperty[ActionHandlers.DrawCardHandler]
  ): Node =
    val cardsView = new VBox()
    cardsView.setId("card-pane")
    cardsView.getStylesheets.addAll("decks.css", "colors.css")

    // Tickets button
    val ticketsButton = new Button(Strings.tickets)
    ticketsButton.getStyleClass.add("gauged")

    // Tickets gauge
    val ticketsGauge = createGauge()
    val ticketsForeground = ticketsGauge.getChildren.get(1).asInstanceOf[Rectangle]
    ticketsForeground.widthProperty.bind(
      gameState.ticketPercentageProperty.multiply(DeckOffset).divide(100)
    )

    ticketsButton.setGraphic(ticketsGauge)
    ticketsButton.disableProperty.bind(drawTicketsHandler.isNull)
    ticketsButton.setOnAction(_ => drawTicketsHandler.get.onDrawTickets())

    cardsView.getChildren.add(ticketsButton)

    // Face-up cards
    for i <- 0 until Constants.FaceUpCardsCount do
      val slot = i
      val faceUpCard = new StackPane()
      faceUpCard.getStyleClass.addAll("card")

      // Bind card color style class
      gameState.faceUpCardProperty(i).addListener { (_, oldCard, newCard) =>
        faceUpCard.getStyleClass.removeIf(s =>
          Card.all.exists(c => s == cardStyleClass(c))
        )
        if newCard != null then faceUpCard.getStyleClass.add(cardStyleClass(newCard))
      }

      val outside = new Rectangle(CardWidth, CardHeight)
      outside.getStyleClass.add("outside")

      val inside = new Rectangle(40, 70)
      inside.getStyleClass.addAll("inside", "filled")

      val trainImage = new Rectangle(40, 70)
      trainImage.getStyleClass.add("train-image")

      faceUpCard.getChildren.addAll(outside, inside, trainImage)
      faceUpCard.disableProperty.bind(drawCardHandler.isNull)
      faceUpCard.setOnMouseClicked(_ => drawCardHandler.get.onDrawCard(slot))

      cardsView.getChildren.add(faceUpCard)

    // Deck button
    val deckButton = new Button(Strings.cards)
    deckButton.getStyleClass.add("gauged")

    val deckGauge = createGauge()
    val deckForeground = deckGauge.getChildren.get(1).asInstanceOf[Rectangle]
    deckForeground.widthProperty.bind(
      gameState.cardPercentageProperty.multiply(DeckOffset).divide(100)
    )

    deckButton.setGraphic(deckGauge)
    deckButton.disableProperty.bind(drawCardHandler.isNull)
    deckButton.setOnAction(_ => drawCardHandler.get.onDrawCard(Constants.DeckSlot))

    cardsView.getChildren.add(deckButton)

    cardsView

  private def createCardPane(card: Card, countProperty: ReadOnlyIntegerProperty): StackPane =
    val cardPane = new StackPane()
    cardPane.getStyleClass.addAll("card", cardStyleClass(card))
    cardPane.visibleProperty.bind(Bindings.greaterThan(countProperty, 0))

    val outside = new Rectangle(CardWidth, CardHeight)
    outside.getStyleClass.add("outside")

    val inside = new Rectangle(40, 70)
    inside.getStyleClass.addAll("inside", "filled")

    val trainImage = new Rectangle(40, 70)
    trainImage.getStyleClass.add("train-image")

    val count = new Text()
    count.getStyleClass.add("count")
    count.textProperty.bind(Bindings.convert(countProperty))
    count.visibleProperty.bind(Bindings.greaterThan(countProperty, 1))

    cardPane.getChildren.addAll(outside, inside, trainImage, count)
    cardPane

  private def createGauge(): Group =
    val gauge = new Group()

    val background = new Rectangle(DeckOffset, 5)
    background.getStyleClass.add("background")

    val foreground = new Rectangle(0, 5)
    foreground.getStyleClass.add("foreground")

    gauge.getChildren.addAll(background, foreground)
    gauge

  private def cardStyleClass(card: Card): String =
    if card == Card.Locomotive then "NEUTRAL"
    else card.color.map(_.toString.toUpperCase).getOrElse("NEUTRAL")
