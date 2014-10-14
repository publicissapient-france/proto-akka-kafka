package fr.xebia.poc

import akka.actor.{Props, Cancellable, Actor}
import akka.cluster.ClusterEvent.{MemberRemoved, MemberUp}
import akka.routing.FromConfig
import fr.xebia.poc.message.{CreditCardNumber, Payment}
import org.scalacheck.Gen
import concurrent.duration._
import collection.convert.wrapAsScala._

class ClusterWatcher extends Actor {

  private var privacyNodes = 0
  private var tokenizerNodes = 0

  var running = Option.empty[Cancellable]
  import context.dispatcher

  def receive: Receive = {
    case MemberUp(member) if member.hasRole("privacy") =>
      privacyNodes += 1
      if (privacyNodes == 1 && tokenizerNodes > 0) {
        start()
      }

    case MemberUp(member) if member.hasRole("tokenizer") =>
      tokenizerNodes += 1
      if (tokenizerNodes == 1 && privacyNodes > 0) {
        start()
      }

    case MemberRemoved(member, _) if member.hasRole("privacy") =>
      privacyNodes -= 1
      if (privacyNodes < 1) {
        stop()
      }

    case MemberRemoved(member, _) if member.hasRole("tokenizer") =>
      tokenizerNodes -= 1
      if (tokenizerNodes < 1) {
        stop()
      }

  }

  val tokenizer = context.system.actorOf(FromConfig.props(), "tokenizer")
  val privacy = context.system.actorOf(FromConfig.props(), "privacy")

  def start() {

    running = Some(context.system.scheduler.schedule(3.second, 250.milliseconds) {

      genPayment.sample.foreach { payment =>
        val paymentActor = context.system.actorOf(Props(classOf[PaymentAggregator], tokenizer, privacy), payment.uuid)
        paymentActor ! payment
      }
    })
  }

  val genPayment: Gen[Payment] = for {
    amount <- Gen.choose(10, 3500)
    nameChars <- Gen.listOf(Gen.alphaUpperChar).map(_.take(20))
    name = nameChars.mkString("")
    creditCardDigits <- Gen.containerOfN(16, Gen.choose(0, 9))
    ccNumber = creditCardDigits.mkString("")
  } yield Payment(CreditCardNumber(ccNumber), amount, name)

  def stop(): Unit = {
    running.foreach(_.cancel())
    running = None
  }
}
