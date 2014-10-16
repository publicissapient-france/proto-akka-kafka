package fr.xebia.poc

import akka.actor.Props
import fr.xebia.poc.core.SeedDiscovery

object StartTokeniser extends App {
  val cluster = SeedDiscovery.joinCluster()

  cluster.system.actorOf(Props[Tokenizer], "tokenizer")
}
