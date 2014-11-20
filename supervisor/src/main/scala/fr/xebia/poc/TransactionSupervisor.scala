package fr.xebia.poc

import akka.actor.{Props, Actor, ActorRef}
import fr.xebia.poc.message.TermColors._
import fr.xebia.poc.message._

class TransactionSupervisor(configuration: ActorRef, blacklist: ActorRef, gatewaybank: ActorRef) extends Actor {

  def receive: Receive = {

    case Transaction(uuid, clientId, card, price) =>

      println(s"${ANSI_GREEN}[NEW]${ANSI_RESET} TransactionRequest($uuid, $clientId, $card, $price)")

      val request = TransactionRequest(uuid, Client(clientId), CreditCardNumber(card), price)

      context.actorOf(Props(classOf[Workflow], sender(), configuration, blacklist, gatewaybank, request), s"workflow-$uuid")

  }

}