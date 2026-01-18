package ch.epfl.tchu.gui

import ch.epfl.tchu.SortedBag
import ch.epfl.tchu.game.*
import ch.epfl.tchu.net.{RemotePlayerClient, RemotePlayerProxy}
import javafx.application.{Application, Platform}
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.VBox
import javafx.stage.{Modality, Stage}

import java.net.ServerSocket
import scala.util.Random

/** Main entry point for the tCHu application. */
object Main:
  private val DefaultPort = 5108

  def main(args: Array[String]): Unit =
    Application.launch(classOf[MainApp], args*)

class MainApp extends Application:
  private val DefaultPort = 5108

  override def start(primaryStage: Stage): Unit =
    // Prevent JavaFX from exiting when no windows are shown
    Platform.setImplicitExit(false)

    // Show language selection dialog
    showLanguageSelection(primaryStage)

    val args = getParameters.getRaw

    if args.isEmpty then
      // No arguments - start as server (host)
      startServer()
    else if args.size == 1 || args.size == 2 then
      // Arguments - start as client
      val hostname = args.get(0)
      val port = if args.size == 2 then args.get(1).toInt else DefaultPort
      startClient(hostname, port)
    else
      System.err.println("Usage:")
      System.err.println("  Server: sbt run")
      System.err.println("  Client: sbt \"run <hostname> [port]\"")
      System.exit(1)

  private def showLanguageSelection(owner: Stage): Unit =
    val dialog = new Stage()
    dialog.initModality(Modality.APPLICATION_MODAL)
    dialog.setTitle("Language / Langue")

    val label = new Label("Select Language / Choisir la langue:")

    val englishBtn = new Button("English")
    englishBtn.setPrefWidth(150)
    englishBtn.setOnAction { _ =>
      Strings.setLanguage(Strings.Language.English)
      dialog.close()
    }

    val frenchBtn = new Button("Francais")
    frenchBtn.setPrefWidth(150)
    frenchBtn.setOnAction { _ =>
      Strings.setLanguage(Strings.Language.French)
      dialog.close()
    }

    val layout = new VBox(15)
    layout.setAlignment(Pos.CENTER)
    layout.setPadding(new Insets(20))
    layout.getChildren.addAll(label, englishBtn, frenchBtn)

    dialog.setScene(new Scene(layout))
    dialog.showAndWait()

  private def startServer(): Unit =
    println(s"Starting tCHu server on port $DefaultPort")
    println("Waiting for remote player to connect...")

    // Run server setup in background thread to not block JavaFX Application Thread
    new Thread(() => {
      val serverSocket = new ServerSocket(DefaultPort)
      val socket = serverSocket.accept()
      println("Remote player connected!")

      // Create players
      val localPlayer: Player = new GraphicalPlayerAdapter()
      val remotePlayer: Player = new RemotePlayerProxy(socket)

      val players = Map[PlayerId, Player](
        PlayerId.Player1 -> localPlayer,
        PlayerId.Player2 -> remotePlayer
      )

      val playerNames = Map[PlayerId, String](
        PlayerId.Player1 -> Strings.you,
        PlayerId.Player2 -> Strings.opponent
      )

      // Start game
      val tickets = SortedBag.of(ChMap.tickets)
      val rng = new Random()
      Game.play(players, playerNames, tickets, rng)
    }).start()

  private def startClient(hostname: String, port: Int): Unit =
    println(s"Connecting to server at $hostname:$port")

    // Run client setup in background thread to not block JavaFX Application Thread
    new Thread(() => {
      val player: Player = new GraphicalPlayerAdapter()
      val client = new RemotePlayerClient(player, hostname, port)

      println("Connected! Starting game...")
      client.run()
    }).start()
