package fr.xebia.poc

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory

object StartTokeniser extends App {
  val system = ActorSystem.create("clustering-cluster", ConfigFactory.load("reference.conf"))
  system.actorOf(Props[Tokenizer], "tokenizer")
}
