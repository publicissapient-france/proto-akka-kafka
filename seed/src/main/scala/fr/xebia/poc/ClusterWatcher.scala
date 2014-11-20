package fr.xebia.poc

import java.util.UUID

import akka.actor.{ActorRef, Actor, Cancellable}
import akka.cluster.ClusterEvent.{MemberRemoved, MemberUp}
import akka.routing.FromConfig
import fr.xebia.poc.message.Transaction
import org.scalacheck.Gen

import scala.collection.convert.wrapAsScala._
import scala.concurrent.duration._

class ClusterWatcher(logger: ActorRef) extends Actor {

  private var supervisorNodes = 0
  private var blacklistNodes = 0
  private var gatewayBankNodes = 0

  var running = Option.empty[Cancellable]
  import context.dispatcher

  def receive: Receive = {

    case MemberUp(member) if member.hasRole("supervisor") =>
      supervisorNodes += 1
      if (supervisorNodes == 1 && blacklistNodes > 0 && gatewayBankNodes > 0) {
        start()
      }

    case MemberUp(member) if member.hasRole("blacklist") =>
      blacklistNodes += 1
      if (blacklistNodes == 1 && supervisorNodes > 0 && gatewayBankNodes > 0) {
        start()
      }

    case MemberUp(member) if member.hasRole("gatewaybank") =>
      gatewayBankNodes += 1
      if (gatewayBankNodes == 1 && supervisorNodes > 0 && blacklistNodes > 0) {
        start()
      }

    case MemberRemoved(member, _) if member.hasRole("supervisor") =>
      supervisorNodes -= 1
      if (blacklistNodes < 1 && gatewayBankNodes < 1) {
        stop()
      }

    case MemberRemoved(member, _) if member.hasRole("blacklist") =>
      blacklistNodes -= 1
      if (supervisorNodes < 1 && gatewayBankNodes < 1) {
        stop()
      }

    case MemberRemoved(member, _) if member.hasRole("gatewaybank") =>
      gatewayBankNodes -= 1
      if (supervisorNodes < 1 && blacklistNodes < 1) {
        stop()
      }

  }

  val supervisor = context.system.actorOf(FromConfig.props(), "supervisor")

  def start() {

    running = Some(context.system.scheduler.schedule(3.second, 250.milliseconds) {

      genTransaction.sample.foreach { transaction =>
        supervisor.tell(transaction, logger)
      }

    })

  }

  val genTransaction: Gen[Transaction] = for {
    clientId <- Gen.choose(0, 200)
    client = "client-" + clientId
    cardDigits <- Gen.containerOfN(16, Gen.choose(0, 9))
    card = cardDigits.mkString("")
    price <-  Gen.choose(10, 3500)
  } yield Transaction(UUID.randomUUID(), client, card, price)


  def stop(): Unit = {
    running.foreach(_.cancel())
    running = None
  }
}
