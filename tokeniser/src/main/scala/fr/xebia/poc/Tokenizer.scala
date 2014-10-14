package fr.xebia.poc

import akka.actor.Actor
import fr.xebia.poc.message.CreditCardNumber
import org.scalacheck.Gen
import concurrent.duration._

class Tokenizer extends Actor {

  def receive: Receive = {
    case creditCardNumber: CreditCardNumber =>
      Gen.identifier.map(_.take(16)).sample.foreach { token =>
        println(s"$creditCardNumber => $token")

        import context.dispatcher
        val requester = sender()
        context.system.scheduler.scheduleOnce(500.milliseconds) {
          requester ! CreditCardNumber(token)
        }
      }
  }
}
