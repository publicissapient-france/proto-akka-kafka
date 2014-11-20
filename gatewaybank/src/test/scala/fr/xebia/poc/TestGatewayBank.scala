package fr.xebia.poc

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import fr.xebia.poc.message._
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class TestGatewayBank extends FunSpec with BeforeAndAfterAll {

  implicit val system = ActorSystem()
  val gatewayBankActor = system.actorOf(Props[GatewayBank], "gatewayBankActor")

  describe("GatewayBank actor tests") {

    it("should accept transaction request") {

      val probe = TestProbe()

      probe.send(gatewayBankActor, TransactionRequest(UUID.randomUUID, Client("1"), CreditCardNumber("0123456789012345"), 10.50))

      probe.expectMsg(PaymentAccepted)

    }

    it("should refuse transaction request") {

      val probe = TestProbe()

      probe.send(gatewayBankActor, TransactionRequest(UUID.randomUUID, Client("1"), CreditCardNumber("1111111111111119"), 19.99))

      probe.expectMsg(PaymentRefused)

    }

  }

  override protected def afterAll() {

    super.afterAll()
    system.shutdown()
    system.awaitTermination()

  }

}
