package fr.xebia.poc

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import fr.xebia.poc.message.{BlacklistRequest, CardAccepted, CardRefused, CreditCardNumber}
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class TestBlacklist extends FunSpec with BeforeAndAfterAll {

  implicit val system = ActorSystem()
  val blacklistActor = system.actorOf(Props[Blacklist], "blacklistActor")

  describe("A blacklist actor") {

    it("should refuse blacklisted card") {

      val probe = TestProbe()

      val creditCardNumber = new CreditCardNumber("0123456789012345")
      probe.send(blacklistActor, BlacklistRequest(creditCardNumber))

      probe.expectMsg(CardRefused)

    }

    it("should accept non-blacklisted card") {

      val probe = TestProbe()

      val creditCardNumber = new CreditCardNumber("1111111111111111")
      probe.send(blacklistActor, BlacklistRequest(creditCardNumber))

      probe.expectMsg(CardAccepted)

    }

  }

  override protected def afterAll() {

    super.afterAll()
    system.shutdown()
    system.awaitTermination()

  }

}
