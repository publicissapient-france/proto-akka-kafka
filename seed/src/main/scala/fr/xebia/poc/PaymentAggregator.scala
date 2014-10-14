package fr.xebia.poc

import akka.actor.{Cancellable, ActorRef}
import akka.persistence.PersistentActor
import fr.xebia.poc.message._
import concurrent.duration._

object PaymentAggregator {
  val ANSI_RED = "\u001B[31m"
  val ANSI_RESET = "\u001B[0m"
  val ANSI_GREEN = "\u001B[32m"
  val ANSI_YELLOW = "\u001B[33m"
}

class PaymentAggregator(tokenizer: ActorRef, privacyer: ActorRef) extends PersistentActor {

  var token = Option.empty[CreditCardNumber]
  var hiddenName = Option.empty[String]

  var tries = 5

  case object Timeout

  import context.dispatcher

  var timeout = Option.empty[Cancellable]

  var payment:Payment = _

  def receiveCommand: Receive = {

    case _payment: Payment =>
      persistAsync(PaymentCreated(_payment)) { _ =>
        println(s"[NEW] : ${_payment}")
        payment = _payment
        tokenizer ! _payment.creditCardNumber
        privacyer ! _payment.name
        timeout = Some(context.system.scheduler.scheduleOnce(5.seconds, self, Timeout))
      }

    case _token: CreditCardNumber =>
      persistAsync(TokenReceived(_token)) { _ =>

        token = Some(_token)
        stopIfOk()
      }

    case privacyName: String =>
      persistAsync(PrivacyReceived(privacyName)) { _ =>

        hiddenName = Some(privacyName)
        stopIfOk()
      }

    case Timeout if tries > 0 =>
      persistAsync(PaymentRetried(tries - 1)) { _ =>
        println(s"${PaymentAggregator.ANSI_YELLOW}[RETRY]${PaymentAggregator.ANSI_RESET} : ${payment.uuid}")
        timeout = Some(context.system.scheduler.scheduleOnce(5.seconds, self, Timeout))
        tries -= 1

        if (token.isEmpty) {
          tokenizer ! payment.creditCardNumber
        }

        if (hiddenName.isEmpty) {
          privacyer ! payment.name
        }
      }


    case Timeout =>
      persistAsync(PaymentRefused) { _ =>
        println(s"${PaymentAggregator.ANSI_RED}[REJECTED]${PaymentAggregator.ANSI_RESET} : ${payment.uuid}")
        context stop self
      }

  }

  def stopIfOk(): Unit = {
    (token, hiddenName) match {
      case (Some(_token), Some(_name)) =>
        val transformedPayment = payment.copy(creditCardNumber = _token, name = _name)
        persistAsync(PaymentValidated) { _ =>
          println(s"${PaymentAggregator.ANSI_GREEN}[ACCEPTED]${PaymentAggregator.ANSI_RESET} : $transformedPayment")
          context stop self
          timeout.foreach(_.cancel())
          timeout = None
        }
      case _ =>
    }
  }

  override def receiveRecover: Receive = {
    case TokenReceived(_token) =>
      token = Some(_token)

    case PrivacyReceived(name) =>
      hiddenName = Some(name)

    case PaymentValidated =>
      context stop self

    case PaymentRefused =>
      context stop self

    case PaymentRetried(remainingAttempt) =>
      tries = remainingAttempt

  }

  val persistenceId: String = s"payment-${self.path.name}"
}