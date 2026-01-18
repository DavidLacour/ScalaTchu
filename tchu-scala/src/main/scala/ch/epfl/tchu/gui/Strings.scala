package ch.epfl.tchu.gui

import ch.epfl.tchu.game.Color

/** Language manager that delegates to the appropriate language strings. */
object Strings:
  enum Language:
    case English, French

  private var currentLanguage: Language = Language.English

  def setLanguage(lang: Language): Unit = currentLanguage = lang
  def getLanguage: Language = currentLanguage

  // Card names
  def blackCard: String = if currentLanguage == Language.French then "noire" else "black"
  def blueCard: String = if currentLanguage == Language.French then "bleue" else "blue"
  def greenCard: String = if currentLanguage == Language.French then "verte" else "green"
  def orangeCard: String = if currentLanguage == Language.French then "orange" else "orange"
  def redCard: String = if currentLanguage == Language.French then "rouge" else "red"
  def violetCard: String = if currentLanguage == Language.French then "violette" else "violet"
  def whiteCard: String = if currentLanguage == Language.French then "blanche" else "white"
  def yellowCard: String = if currentLanguage == Language.French then "jaune" else "yellow"
  def locomotiveCard: String = if currentLanguage == Language.French then "locomotive" else "locomotive"

  // Button labels
  def tickets: String = if currentLanguage == Language.French then "Billets" else "Tickets"
  def cards: String = if currentLanguage == Language.French then "Cartes" else "Cards"
  def choose: String = if currentLanguage == Language.French then "Choisir" else "Choose"

  // Window titles
  def ticketsChoice: String = if currentLanguage == Language.French then "Choix de billets" else "Ticket Selection"
  def cardsChoice: String = if currentLanguage == Language.French then "Choix de cartes" else "Card Selection"

  // Prompts
  def chooseTickets: String =
    if currentLanguage == Language.French then "Choisissez au moins %s billet%s parmi ceux-ci :"
    else "Choose at least %s ticket%s from these:"

  def chooseCards: String =
    if currentLanguage == Language.French then "Choisissez les cartes à utiliser pour vous emparer de cette route :"
    else "Choose the cards to use to claim this route:"

  def chooseAdditionalCards: String =
    if currentLanguage == Language.French then "Choisissez les cartes supplémentaires à utiliser pour vous emparer de ce tunnel (ou aucune pour annuler et passer votre tour) :"
    else "Choose the additional cards to use to claim this tunnel (or none to cancel and pass your turn):"

  // Game progress
  def willPlayFirst: String =
    if currentLanguage == Language.French then "%s jouera en premier.\n\n"
    else "%s will play first.\n\n"

  def keptNTickets: String =
    if currentLanguage == Language.French then "%s a gardé %s billet%s.\n"
    else "%s kept %s ticket%s.\n"

  def canPlay: String =
    if currentLanguage == Language.French then "\nC'est à %s de jouer.\n"
    else "\nIt's %s's turn to play.\n"

  def drewTickets: String =
    if currentLanguage == Language.French then "%s a tiré %s billet%s...\n"
    else "%s drew %s ticket%s...\n"

  def drewBlindCard: String =
    if currentLanguage == Language.French then "%s a tiré une carte de la pioche.\n"
    else "%s drew a card from the deck.\n"

  def drewVisibleCard: String =
    if currentLanguage == Language.French then "%s a tiré une carte %s visible.\n"
    else "%s drew a visible %s card.\n"

  def claimedRoute: String =
    if currentLanguage == Language.French then "%s a pris possession de la route %s au moyen de %s.\n"
    else "%s claimed the route %s using %s.\n"

  def attemptsTunnelClaim: String =
    if currentLanguage == Language.French then "%s tente de s'emparer du tunnel %s au moyen de %s !\n"
    else "%s attempts to claim the tunnel %s using %s!\n"

  def additionalCardsAre: String =
    if currentLanguage == Language.French then "Les cartes supplémentaires sont %s. "
    else "The additional cards are %s. "

  def noAdditionalCost: String =
    if currentLanguage == Language.French then "Elles n'impliquent aucun coût additionnel.\n"
    else "They imply no additional cost.\n"

  def someAdditionalCost: String =
    if currentLanguage == Language.French then "Elles impliquent un coût additionnel de %s carte%s.\n"
    else "They imply an additional cost of %s card%s.\n"

  def didNotClaimRoute: String =
    if currentLanguage == Language.French then "%s n'a pas pu (ou voulu) s'emparer de la route %s.\n"
    else "%s could not (or did not want to) claim the route %s.\n"

  def lastTurnBegins: String =
    if currentLanguage == Language.French then "\n%s n'a plus que %s wagon%s, le dernier tour commence !\n"
    else "\n%s only has %s car%s left, the last turn begins!\n"

  def getsBonus: String =
    if currentLanguage == Language.French then "\n%s reçoit un bonus de 10 points pour le plus long trajet (%s).\n"
    else "\n%s receives a 10 point bonus for the longest trail (%s).\n"

  def wins: String =
    if currentLanguage == Language.French then "\n%s remporte la victoire avec %s point%s, contre %s point%s !\n"
    else "\n%s wins with %s point%s, against %s point%s!\n"

  def draw: String =
    if currentLanguage == Language.French then "\n%s sont ex æqo avec %s points !\n"
    else "\n%s are tied with %s points!\n"

  // Player stats
  def playerStats: String =
    if currentLanguage == Language.French then " %s :\n– %s billets,\n– %s cartes,\n– %s wagons,\n– %s points."
    else " %s:\n– %s tickets,\n– %s cards,\n– %s cars,\n– %s points."

  // Separators
  def andSeparator: String = if currentLanguage == Language.French then " et " else " and "
  def enDashSeparator: String = " – "

  // Player names
  def you: String = if currentLanguage == Language.French then "Joueur serveur" else "Server's player"
  def opponent: String = if currentLanguage == Language.French then "Joueur client" else "Client's player"
  def player1: String = if currentLanguage == Language.French then "Joueur 1" else "Player 1"
  def player2: String = if currentLanguage == Language.French then "Joueur 2" else "Player 2"

  def plural(value: Int): String = if math.abs(value) == 1 then "" else "s"
