package fr.xebia.poc

import java.util.UUID

import akka.actor.{Props, ActorSystem}
import akka.testkit.TestProbe
import fr.xebia.poc.message._
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class TestWorkflow extends FunSpec with BeforeAndAfterAll {

  implicit val system = ActorSystem()

  describe("Workflow actor tests") {

    it("should reply TransactionAccepted when features is empty") {

      val request = TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("1111111111111111"), 10.0)
      val probe = TestProbe()
      val origin = TestProbe()
      val configuration = TestProbe()
      val blacklist = TestProbe()
      val gatewayBank = TestProbe()
      val workflowActor = system.actorOf(Props(classOf[Workflow], origin.ref, configuration.ref, blacklist.ref, gatewayBank.ref, request), "workflowActor")

      probe.send(workflowActor, ConfigurationReply(Set.empty[Feature]))

      origin.expectMsg(TransactionAccepted)

    }

    it("should reply TransactionAccepted when BlacklistEnabled and CardAccepted") {

      val request = TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("1111111111111111"), 10.0)
      val probe = TestProbe()
      val origin = TestProbe()
      val configuration = TestProbe()
      val blacklist = TestProbe()
      val gatewayBank = TestProbe()
      val workflowActor = system.actorOf(Props(classOf[Workflow], origin.ref, configuration.ref, blacklist.ref, gatewayBank.ref, request), "workflowActor")

      probe.send(workflowActor, ConfigurationReply(Set(BlacklistEnabled)))

      blacklist.expectMsg(BlacklistRequest(request.card))
      blacklist.send(workflowActor, CardAccepted)

      origin.expectMsg(TransactionAccepted)

    }

    it("should reply TransactionRefused when BlacklistEnabled and CardRefused") {

      val request = TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("1111111111111111"), 10.0)
      val probe = TestProbe()
      val origin = TestProbe()
      val configuration = TestProbe()
      val blacklist = TestProbe()
      val gatewayBank = TestProbe()
      val workflowActor = system.actorOf(Props(classOf[Workflow], origin.ref, configuration.ref, blacklist.ref, gatewayBank.ref, request), "workflowActor")

      probe.send(workflowActor, ConfigurationReply(Set(BlacklistEnabled)))

      blacklist.expectMsg(BlacklistRequest(request.card))
      blacklist.send(workflowActor, CardRefused)

      origin.expectMsg(TransactionRefused)

    }

    it("should reply TransactionAccepted when GatewayEnabled and PaymentAccepted") {

      val request = TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("1111111111111111"), 10.0)
      val probe = TestProbe()
      val origin = TestProbe()
      val configuration = TestProbe()
      val blacklist = TestProbe()
      val gatewayBank = TestProbe()
      val workflowActor = system.actorOf(Props(classOf[Workflow], origin.ref, configuration.ref, blacklist.ref, gatewayBank.ref, request), "workflowActor")

      probe.send(workflowActor, ConfigurationReply(Set(GatewayBankEnabled)))

      gatewayBank.expectMsg(request)
      gatewayBank.send(workflowActor, PaymentAccepted)

      origin.expectMsg(TransactionAccepted)

    }

    it("should reply TransactionRefused when GatewayEnabled and PaymentRefused") {

      val request = TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("1111111111111111"), 10.0)
      val probe = TestProbe()
      val origin = TestProbe()
      val configuration = TestProbe()
      val blacklist = TestProbe()
      val gatewayBank = TestProbe()
      val workflowActor = system.actorOf(Props(classOf[Workflow], origin.ref, configuration.ref, blacklist.ref, gatewayBank.ref, request), "workflowActor")

      probe.send(workflowActor, ConfigurationReply(Set(GatewayBankEnabled)))

      gatewayBank.expectMsg(request)
      gatewayBank.send(workflowActor, PaymentRefused)

      origin.expectMsg(TransactionRefused)

    }

    it("should reply TransactionAccepted when BlacklistEnabled and GatewayEnabled and CardAccepted and PaymentAccepted") {

      val request = TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("1111111111111111"), 10.0)
      val probe = TestProbe()
      val origin = TestProbe()
      val configuration = TestProbe()
      val blacklist = TestProbe()
      val gatewayBank = TestProbe()
      val workflowActor = system.actorOf(Props(classOf[Workflow], origin.ref, configuration.ref, blacklist.ref, gatewayBank.ref, request), "workflowActor")

      probe.send(workflowActor, ConfigurationReply(Set(BlacklistEnabled, GatewayBankEnabled)))

      blacklist.expectMsg(BlacklistRequest(request.card))
      blacklist.send(workflowActor, CardAccepted)

      gatewayBank.expectMsg(request)
      gatewayBank.send(workflowActor, PaymentAccepted)

      origin.expectMsg(TransactionAccepted)

    }

    it("should reply TransactionRefused when BlacklistEnabled and GatewayEnabled and CardAccepted and PaymentRefused") {

      val request = TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("1111111111111111"), 10.0)
      val probe = TestProbe()
      val origin = TestProbe()
      val configuration = TestProbe()
      val blacklist = TestProbe()
      val gatewayBank = TestProbe()
      val workflowActor = system.actorOf(Props(classOf[Workflow], origin.ref, configuration.ref, blacklist.ref, gatewayBank.ref, request), "workflowActor")

      probe.send(workflowActor, ConfigurationReply(Set(BlacklistEnabled, GatewayBankEnabled)))

      blacklist.expectMsg(BlacklistRequest(request.card))
      blacklist.send(workflowActor, CardAccepted)

      gatewayBank.expectMsg(request)
      gatewayBank.send(workflowActor, PaymentRefused)

      origin.expectMsg(TransactionRefused)

    }

    it("should reply TransactionRefused when BlacklistEnabled and GatewayEnabled and CardRefused") {

      val request = TransactionRequest(UUID.randomUUID, Client("0001"), CreditCardNumber("1111111111111111"), 10.0)
      val probe = TestProbe()
      val origin = TestProbe()
      val configuration = TestProbe()
      val blacklist = TestProbe()
      val gatewayBank = TestProbe()
      val workflowActor = system.actorOf(Props(classOf[Workflow], origin.ref, configuration.ref, blacklist.ref, gatewayBank.ref, request), "workflowActor")

      probe.send(workflowActor, ConfigurationReply(Set(BlacklistEnabled, GatewayBankEnabled)))

      blacklist.expectMsg(BlacklistRequest(request.card))
      blacklist.send(workflowActor, CardRefused)

      origin.expectMsg(TransactionRefused)

    }

  }

  override protected def afterAll() {

    super.afterAll()
    system.shutdown()
    system.awaitTermination()

  }

}

