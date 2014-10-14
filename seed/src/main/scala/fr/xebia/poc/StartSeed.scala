package fr.xebia.poc

import akka.actor._
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.{Cluster, ClusterEvent}
import com.typesafe.config.ConfigFactory


object StartSeed extends App {

  val system = ActorSystem.create("clustering-cluster", ConfigFactory.load("reference.conf"))

  val clusterWatcher = system.actorOf(Props(classOf[ClusterWatcher]), "cluster-watcher")
  Cluster(system).subscribe(clusterWatcher, ClusterEvent.initialStateAsEvents, classOf[MemberEvent])

}