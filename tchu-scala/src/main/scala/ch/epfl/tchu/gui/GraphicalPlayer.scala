package ch.epfl.tchu.gui

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.*
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.{Button, ListView, SelectionMode}
import javafx.scene.control.cell.TextFieldListCell
import javafx.scene.layout.{BorderPane, VBox}
import javafx.scene.text.{Text, TextFlow}
import javafx.stage.{Modality, Stage, StageStyle}
import scala.jdk.CollectionConverters.*

/** Graphical player interface using JavaFX. */
final class GraphicalPlayer(playerId: PlayerId, playerNames: Map[PlayerId, String]):

  private val gameState = new ObservableGameState(playerId)
  private val gameMessages = FXCollections.observableArrayList[Text]()
  private val mainStage = new Stage()

  private val drawTicketsHandler = new SimpleObjectProperty[ActionHandlers.DrawTicketsHandler]()
  private val drawCardHandler = new SimpleObjectProperty[ActionHandlers.DrawCardHandler]()
  private val claimRouteHandler = new SimpleObjectProperty[ActionHandlers.ClaimRouteHandler]()

  // Initialize the GUI
  locally {
    val cardChooser: MapViewCreator.CardChooser = (options, handler) => chooseClaimCards(options, handler)

    val mapView = MapViewCreator.createMapView(gameState, claimRouteHandler, cardChooser)
    val cardsView = DecksViewCreator.createCardsView(gameState, drawTicketsHandler, drawCardHandler)
    val handView = DecksViewCreator.createHandView(gameState)
    val infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, gameMessages)

    val mainPane = new BorderPane()
    mainPane.setCenter(mapView)
    mainPane.setRight(cardsView)
    mainPane.setBottom(handView)
    mainPane.setLeft(infoView)

    val scene = new Scene(mainPane)
    mainStage.setScene(scene)
    mainStage.setTitle(s"tCHu - ${playerNames(playerId)}")
    mainStage.setMinWidth(1200)
    mainStage.setMinHeight(800)
    mainStage.show()
  }

  /** Updates the game state. */
  def setState(publicGameState: PublicGameState, playerState: PlayerState): Unit =
    Platform.runLater(() => gameState.setState(publicGameState, playerState))

  /** Displays a message in the info area. */
  def receiveInfo(message: String): Unit =
    Platform.runLater { () =>
      val text = new Text(message + "\n")
      if gameMessages.size >= 5 then gameMessages.remove(0)
      gameMessages.add(text)
    }

  /** Starts the player's turn with the given action handlers. */
  def startTurn(
      drawTickets: ActionHandlers.DrawTicketsHandler,
      drawCards: ActionHandlers.DrawCardHandler,
      claimRoute: ActionHandlers.ClaimRouteHandler
  ): Unit =
    Platform.runLater { () =>
      if gameState.canDrawTickets then
        drawTicketsHandler.set(() => {
          drawTicketsHandler.set(null)
          drawCardHandler.set(null)
          claimRouteHandler.set(null)
          drawTickets.onDrawTickets()
        })
      if gameState.canDrawCards then
        drawCardHandler.set(slot => {
          drawTicketsHandler.set(null)
          drawCardHandler.set(null)
          claimRouteHandler.set(null)
          drawCards.onDrawCard(slot)
        })
      claimRouteHandler.set((route, cards) => {
        drawTicketsHandler.set(null)
        drawCardHandler.set(null)
        claimRouteHandler.set(null)
        claimRoute.onClaimRoute(route, cards)
      })
    }

  /** Lets the player choose tickets. */
  def chooseTickets(tickets: SortedBag[Ticket], handler: ActionHandlers.ChooseTicketsHandler): Unit =
    Platform.runLater { () =>
      val dialog = createTicketChoiceDialog(tickets, handler)
      dialog.show()
    }

  /** Lets the player draw a card after already drawing one. */
  def drawCard(handler: ActionHandlers.DrawCardHandler): Unit =
    Platform.runLater { () =>
      drawCardHandler.set(slot => {
        drawCardHandler.set(null)
        claimRouteHandler.set(null)
        handler.onDrawCard(slot)
      })
    }

  /** Lets the player choose additional cards for a tunnel claim. */
  def chooseAdditionalCards(options: List[SortedBag[Card]], handler: ActionHandlers.ChooseCardsHandler): Unit =
    Platform.runLater { () =>
      val dialog = createCardChoiceDialog(options, handler)
      dialog.show()
    }

  private def chooseClaimCards(options: List[SortedBag[Card]], handler: ActionHandlers.ChooseCardsHandler): Unit =
    if options.size == 1 then handler.onChooseCards(options.head)
    else
      Platform.runLater { () =>
        val dialog = createCardChoiceDialog(options, handler)
        dialog.show()
      }

  private def createTicketChoiceDialog(tickets: SortedBag[Ticket], handler: ActionHandlers.ChooseTicketsHandler): Stage =
    val dialog = new Stage(StageStyle.UTILITY)
    dialog.initOwner(mainStage)
    dialog.initModality(Modality.WINDOW_MODAL)
    dialog.setTitle(Strings.ticketsChoice)

    val content = new VBox()

    val intro = new TextFlow(new Text(
      Strings.chooseTickets.format(tickets.size - Constants.DiscardableTicketsCount, Strings.plural(tickets.size))
    ))

    val listView = new ListView[Ticket](FXCollections.observableArrayList(tickets.toList.asJava))
    listView.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

    val button = new Button(Strings.choose)
    button.disableProperty.bind(
      Bindings.size(listView.getSelectionModel.getSelectedItems)
        .lessThan(tickets.size - Constants.DiscardableTicketsCount)
    )
    button.setOnAction { _ =>
      val chosen = SortedBag.of(listView.getSelectionModel.getSelectedItems.asScala)
      dialog.hide()
      handler.onChooseTickets(chosen)
    }

    content.getChildren.addAll(intro, listView, button)
    dialog.setScene(new Scene(content))
    dialog

  private def createCardChoiceDialog(options: List[SortedBag[Card]], handler: ActionHandlers.ChooseCardsHandler): Stage =
    val dialog = new Stage(StageStyle.UTILITY)
    dialog.initOwner(mainStage)
    dialog.initModality(Modality.WINDOW_MODAL)
    dialog.setTitle(Strings.cardsChoice)

    val content = new VBox()

    val intro = new TextFlow(new Text(Strings.chooseCards))

    val converter = new CardBagStringConverter()
    val listView = new ListView[SortedBag[Card]](FXCollections.observableArrayList(options.asJava))
    listView.setCellFactory(_ => new TextFieldListCell(converter))

    val button = new Button(Strings.choose)
    button.disableProperty.bind(listView.getSelectionModel.selectedItemProperty.isNull)
    button.setOnAction { _ =>
      val chosen = listView.getSelectionModel.getSelectedItem
      dialog.hide()
      handler.onChooseCards(if chosen != null then chosen else SortedBag.of[Card])
    }

    content.getChildren.addAll(intro, listView, button)
    dialog.setScene(new Scene(content))
    dialog
