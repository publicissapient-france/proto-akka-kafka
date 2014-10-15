package fr.xebia.poc

import akka.actor._
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.{Cluster, ClusterEvent}
import com.typesafe.config.ConfigFactory
import fr.xebia.poc.core.SeedDiscovery


object StartSeed extends App {

  val system = ActorSystem.create("clustering-cluster", ConfigFactory.load("reference.conf"))
  SeedDiscovery.joinCluster(system)
  val cluster = Cluster(system)

  val clusterWatcher = system.actorOf(Props(classOf[ClusterWatcher]), "cluster-watcher")
  cluster.subscribe(clusterWatcher, ClusterEvent.initialStateAsEvents, classOf[MemberEvent])

}