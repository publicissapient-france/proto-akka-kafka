package fr.xebia.poc

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import fr.xebia.poc.core.SeedDiscovery

object StartTokeniser extends App {
  val system = ActorSystem.create("clustering-cluster", ConfigFactory.load("reference.conf"))
  SeedDiscovery.joinCluster(system)

  system.actorOf(Props[Tokenizer], "tokenizer")
}
