package fr.xebia.poc

import akka.actor.Actor
import fr.xebia.poc.message._
import scala.io.Source

class GatewayBank extends Actor {

  def receive: Receive = {

    case PaymentRequest(request) =>

      println(s"Payment request => $request.digits")

      val response = request.digits.last match {
        case '7' | '8' | '9' => PaymentRefused
        case _ => PaymentAccepted
      }

      sender() ! response

  }

}
