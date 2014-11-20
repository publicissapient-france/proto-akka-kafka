package fr.xebia.poc

import akka.actor._
import akka.routing.FromConfig
import fr.xebia.poc.core.SeedDiscovery

object StartTransactionSupervisor extends App {

  val cluster = SeedDiscovery.joinCluster()

  val configuration = cluster.system.actorOf(Props[Configuration], "configuration")
  val blacklist = cluster.system.actorOf(FromConfig.props(), "blacklist")
  val gatewaybank = cluster.system.actorOf(FromConfig.props(), "gatewaybank")

  cluster.system.actorOf(Props(classOf[TransactionSupervisor], configuration, blacklist, gatewaybank), "supervisor")

}