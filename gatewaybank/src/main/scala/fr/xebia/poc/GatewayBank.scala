package fr.xebia.poc

import akka.actor.Actor
import fr.xebia.poc.message.TermColors._
import fr.xebia.poc.message._

class GatewayBank extends Actor {

  def receive: Receive = {

    case TransactionRequest(uuid, client, card, price) =>

      val response = card.digits.last match {
        case '7' | '8' | '9' =>
          println(s"${ANSI_RED}[KO]${ANSI_RESET} PaymentRefused (${uuid})")
          PaymentRefused
        case _ =>
          println(s"${ANSI_GREEN}[OK]${ANSI_RESET} PaymentAccepted (${uuid})")
          PaymentAccepted
      }

      sender() ! response

  }

}
