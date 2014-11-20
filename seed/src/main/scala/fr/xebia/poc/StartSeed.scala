package fr.xebia.poc

import akka.actor._
import akka.cluster.ClusterEvent
import akka.cluster.ClusterEvent.MemberEvent
import fr.xebia.poc.core.SeedDiscovery


object StartSeed extends App {

  val cluster = SeedDiscovery.joinCluster()

  val logger = cluster.system.actorOf(Props(classOf[Logger]), "logger")
  val clusterWatcher = cluster.system.actorOf(Props(classOf[ClusterWatcher], logger), "cluster-watcher")

  cluster.subscribe(clusterWatcher, ClusterEvent.initialStateAsEvents, classOf[MemberEvent])

}