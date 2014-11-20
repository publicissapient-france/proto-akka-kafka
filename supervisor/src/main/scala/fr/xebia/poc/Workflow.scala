package fr.xebia.poc

import akka.actor.{Actor, ActorRef}
import fr.xebia.poc.message.TermColors._
import fr.xebia.poc.message._

class Workflow(origin: ActorRef, configuration: ActorRef, blacklist: ActorRef, gatewaybank: ActorRef, request: TransactionRequest) extends Actor {

  override def preStart() = {

    println(s"${ANSI_GREEN}[NEW]${ANSI_RESET} Workflow(${request.uuid}, ${request.client}, ${request.card}, ${request.price})")

    configuration ! request

  }

  def receive: Receive = {

    case ConfigurationReply(features) =>

      if (features.isEmpty) {

        origin ! TransactionAccepted
        context stop self

      } else if(features == Set(BlacklistEnabled, GatewayBankEnabled)) {

        val workflow:Receive = {
          case CardRefused =>
            origin ! TransactionRefused
            context stop self
          case CardAccepted =>
            gatewaybank ! request
          case PaymentRefused =>
            origin ! TransactionRefused
            context stop self
          case PaymentAccepted =>
            origin ! TransactionAccepted
            context stop self
        }

        blacklist ! BlacklistRequest(request.card)

        context become workflow

      } else if(features == Set(BlacklistEnabled)) {

        val workflow:Receive = {
          case CardRefused =>
            origin ! TransactionRefused
            context stop self
          case CardAccepted =>
            origin ! TransactionAccepted
            context stop self
        }

        blacklist ! BlacklistRequest(request.card)

        context become workflow

      } else if(features == Set(GatewayBankEnabled)) {

        val workflow:Receive = {
          case PaymentRefused =>
            origin ! TransactionRefused
            context stop self
          case PaymentAccepted =>
            origin ! TransactionAccepted
            context stop self
        }

        gatewaybank ! request

        context become workflow

      }

    }

}
