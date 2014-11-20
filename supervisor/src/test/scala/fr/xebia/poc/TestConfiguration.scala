package fr.xebia.poc

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import fr.xebia.poc.message._
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class TestConfiguration extends FunSpec with BeforeAndAfterAll {

  implicit val system = ActorSystem()
  val configurationActor = system.actorOf(Props[Configuration], "configurationActor")

  describe("Configuration actor tests") {

    it("should reply with feature gatewayBankEnabled") {

      val probe = TestProbe()

      probe.send(configurationActor, TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("0123456789012345"), 10.0))

      probe.expectMsg(ConfigurationReply(Set(GatewayBankEnabled)))

    }

    it("should reply with feature blacklistEnabled & gatewayBankEnabled") {

      val probe = TestProbe()

      probe.send(configurationActor, TransactionRequest(UUID.randomUUID, Client("0003"), CreditCardNumber("0123456789012345"), 10.0))

      probe.expectMsg(ConfigurationReply(Set(BlacklistEnabled, GatewayBankEnabled)))

    }

    it("should reply with empty feature") {

      val probe = TestProbe()

      probe.send(configurationActor, TransactionRequest(UUID.randomUUID, Client("0006"), CreditCardNumber("0123456789012345"), 10.0))

      probe.expectMsg(ConfigurationReply(Set.empty[Feature]))

    }

  }

  override protected def afterAll() {

    super.afterAll()
    system.shutdown()
    system.awaitTermination()

  }

}
