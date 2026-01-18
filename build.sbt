val scala3Version = "3.8.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "tchu-scala",
    version := "1.0.0",
    scalaVersion := scala3Version,

    // JavaFX settings
    libraryDependencies ++= Seq(
      "org.openjfx" % "javafx-controls" % "21" classifier osName,
      "org.openjfx" % "javafx-fxml" % "21" classifier osName,
      "org.openjfx" % "javafx-graphics" % "21" classifier osName,
      "org.openjfx" % "javafx-base" % "21" classifier osName,
    ),

    // Testing
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,

    // Compiler options
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-new-syntax",
      "-indent"
    ),

    // Fork for JavaFX
    fork := true,

    // Assembly settings for fat JAR
    assembly / mainClass := Some("ch.epfl.tchu.gui.Main"),
    assembly / assemblyJarName := "tchu.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "module-info.class" => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )

// Detect OS for JavaFX
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}
