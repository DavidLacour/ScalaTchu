package ch.epfl.tchu.gui

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.{Card, ChMap, PlayerId, Route}
import javafx.beans.property.ObjectProperty
import javafx.scene.{Group, Node}
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.shape.{Circle, Rectangle}
import scala.jdk.CollectionConverters.*

/** Creates the map view showing the game board. */
object MapViewCreator:

  /** Functional interface for choosing cards to claim a route. */
  trait CardChooser:
    def chooseCards(options: List[SortedBag[Card]], handler: ActionHandlers.ChooseCardsHandler): Unit

  /** Creates the map view. */
  def createMapView(
      gameState: ObservableGameState,
      claimRouteHandler: ObjectProperty[ActionHandlers.ClaimRouteHandler],
      cardChooser: CardChooser
  ): Node =
    val mapView = new Pane()
    mapView.getStylesheets.addAll("map.css", "colors.css")

    // Add background image
    val background = new ImageView()
    background.setId("map-background")
    mapView.getChildren.add(background)

    // Add route groups
    for route <- ChMap.routes do
      val routeGroup = createRouteGroup(route, gameState, claimRouteHandler, cardChooser)
      mapView.getChildren.add(routeGroup)

    mapView

  private def createRouteGroup(
      route: Route,
      gameState: ObservableGameState,
      claimRouteHandler: ObjectProperty[ActionHandlers.ClaimRouteHandler],
      cardChooser: CardChooser
  ): Group =
    val routeGroup = new Group()
    routeGroup.setId(route.id)
    routeGroup.getStyleClass.addAll(
      "route",
      route.level.toString.toUpperCase,
      route.color.map(_.toString.toUpperCase).getOrElse("NEUTRAL")
    )

    // Bind style class to owner
    gameState.routeOwnerProperty(route).addListener { (_, oldOwner, newOwner) =>
      if oldOwner != null then routeGroup.getStyleClass.remove(playerIdStyleClass(oldOwner))
      if newOwner != null then routeGroup.getStyleClass.add(playerIdStyleClass(newOwner))
    }

    // Add route cells
    for i <- 1 to route.length do
      val cellGroup = createCellGroup(route, i)
      routeGroup.getChildren.add(cellGroup)

    // Disable if can't claim
    routeGroup.disableProperty.bind(
      claimRouteHandler.isNull.or(gameState.canClaimRouteProperty(route).not)
    )

    // Handle click
    routeGroup.setOnMouseClicked { _ =>
      val possibleClaimCards = gameState.possibleClaimCards(route)
      if possibleClaimCards.size == 1 then
        claimRouteHandler.get.onClaimRoute(route, possibleClaimCards.head)
      else
        cardChooser.chooseCards(possibleClaimCards, cards =>
          claimRouteHandler.get.onClaimRoute(route, cards)
        )
    }

    routeGroup

  private def createCellGroup(route: Route, cellIndex: Int): Group =
    val cellGroup = new Group()
    cellGroup.setId(s"${route.id}_$cellIndex")

    val track = new Rectangle(36, 12)
    track.getStyleClass.addAll("track", "filled")

    val wagon = new Group()
    wagon.getStyleClass.add("car")

    val wagonRect = new Rectangle(32, 8)
    wagonRect.getStyleClass.add("filled")

    val wheel1 = new Circle(3)
    wheel1.setCenterX(12)
    wheel1.setCenterY(8)

    val wheel2 = new Circle(3)
    wheel2.setCenterX(24)
    wheel2.setCenterY(8)

    wagon.getChildren.addAll(wagonRect, wheel1, wheel2)
    cellGroup.getChildren.addAll(track, wagon)

    cellGroup

  private def playerIdStyleClass(playerId: PlayerId): String =
    playerId match
      case PlayerId.Player1 => "PLAYER_1"
      case PlayerId.Player2 => "PLAYER_2"
