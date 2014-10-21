package fr.xebia.poc

import akka.actor.Actor

import scala.concurrent.duration._

class Anonymiser extends Actor {

  def receive: Receive = {
    case name: String =>
      val replaced = name.replaceAll(".", "Y")
      println(s"$name => $replaced")

      import context.dispatcher
      val requester = sender()
      context.system.scheduler.scheduleOnce(500.milliseconds) {
        requester ! replaced
      }
  }
}
