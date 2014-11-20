package fr.xebia.poc

import akka.actor.Actor
import fr.xebia.poc.message.TermColors._
import fr.xebia.poc.message._

class Configuration extends Actor {

  def receive: Receive = {

    case TransactionRequest(_, client, _, _) =>

      val features = client.clientId.last match {
        case '0' | '1' | '2' => Set(GatewayBankEnabled : Feature)
        case '3' | '4' | '5' => Set(BlacklistEnabled : Feature, GatewayBankEnabled : Feature)
        case _ => Set.empty[Feature]
      }

      println(s"${ANSI_YELLOW}[REPLY]${ANSI_RESET} ConfigurationReply(${features.mkString(",")})")

      sender() ! ConfigurationReply(features)

  }

}
