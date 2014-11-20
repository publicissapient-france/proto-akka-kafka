package fr.xebia.poc

import akka.actor.Actor
import fr.xebia.poc.message.TermColors._
import fr.xebia.poc.message.{TransactionAccepted, TransactionRefused}

class Logger extends Actor {

  override def receive: Receive = {
    case TransactionAccepted =>
      println(s"${ANSI_GREEN}[OK]${ANSI_RESET} TransactionAccepted (${sender().path})")
    case TransactionRefused =>
      println(s"${ANSI_RED}[KO]${ANSI_RESET} TransactionRefused (${sender().path})")

  }

}
