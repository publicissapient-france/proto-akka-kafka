package fr.xebia.poc

import akka.actor._
import fr.xebia.poc.core.SeedDiscovery

object StartAnonymiser extends App {

  val cluster = SeedDiscovery.joinCluster()

  cluster.system.actorOf(Props[Anonymiser], "privacy")

}