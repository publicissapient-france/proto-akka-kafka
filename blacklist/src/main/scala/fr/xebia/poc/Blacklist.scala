package fr.xebia.poc

import akka.actor.Actor
import fr.xebia.poc.message.TermColors._
import fr.xebia.poc.message._
import scala.io.Source

class Blacklist extends Actor {

  val CSVFilePath = "/blacklist.csv"
  val CSVFieldSepartor = ";"
  val CSVCardNumberHeaderLabel = "CreditCardNumber"
  var blacklistedCards = Set.empty[CreditCardNumber]

  override def preStart(): Unit = {
    val source = Source.fromURL(getClass.getResource(CSVFilePath))
    val lines = source.getLines()
    val headerFields = lines.next().split(CSVFieldSepartor).toList
    val cardNumberIndex = headerFields.indexOf(CSVCardNumberHeaderLabel)
    blacklistedCards = lines.map(_.split(CSVFieldSepartor)(cardNumberIndex)).map(CreditCardNumber).toSet;

  }

  def receive: Receive = {
    case BlacklistRequest(request) =>

      val exist = blacklistedCards(request)
      val response =
        if (!exist) {
          println(s"${ANSI_GREEN}[OK]${ANSI_RESET} CardAccepted (${request.digits})")
          CardAccepted
        } else {
          println(s"${ANSI_RED}[KO]${ANSI_RESET} CardRefused (${request.digits})")
          CardRefused
        }

      sender() ! response

  }

}
