package fr.xebia.poc

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

      val creditCardNumber = CreditCardNumber("0123456789012345")
      probe.send(gatewayBankActor, TransactionRequest(creditCardNumber))

      probe.expectMsg(TransactionAccepted)

    }

    it("should refuse transaction request") {

      val probe = TestProbe()

      val creditCardNumber = CreditCardNumber("1111111111111119")
      probe.send(gatewayBankActor, TransactionRequest(creditCardNumber))

      probe.expectMsg(TransactionRefused)

    }

  }

  override protected def afterAll() {

    super.afterAll()
    system.shutdown()
    system.awaitTermination()

  }

}
