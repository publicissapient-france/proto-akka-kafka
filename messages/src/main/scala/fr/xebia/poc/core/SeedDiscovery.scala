package fr.xebia.poc.core

import java.net.URL

import akka.actor.{ActorSystem, Address}
import akka.cluster.Cluster
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.collection.convert.wrapAsScala._
import scala.collection.immutable
import scala.io.Source

object SeedDiscovery {

  private val extractIp = """ip-([\d-]*)\..*""".r
  private val marathonHost = "54.195.251.225"
  private val marathonPort = 8080

  def joinCluster(system: ActorSystem): Unit = {

    val cluster = Cluster(system)
    val seedNodes = if (System.getenv.isDefinedAt("SEED_NODES")) {
      println( s"""Starting with seed-nodes from env variable $$SEED_NODES""")
      immutable.Seq.empty[Address]
    } else {
      println(s"Discovering seed-nodes from marathon  ($marathonHost,$marathonPort)")
      SeedDiscovery.seedNodesFromMarathon(system.name, marathonHost, marathonPort)
    }

    if (seedNodes.isEmpty) {
      println("First to join the cluster, no seed-nodes found")
    } else {
      println( s"""Joining the cluster with seed-nodes [${seedNodes.mkString(", ")}]""")
      cluster.joinSeedNodes(seedNodes)
    }


  }

  private def seedNodesFromMarathon(systemName: String, marathonHost: String, marathonPort: Int): immutable.Seq[Address] = {
    val marathonWS = Source.fromURL(new URL("http", marathonHost, marathonPort, "/v2/apps/akka-cluster-seed/tasks"))

    val reply = marathonWS.getLines().toList.mkString("")
    val tasks: JValue = parse(reply)

    tasks.children.collect {
      case array: JArray => array
    }.flatMap(_.values).collect {
      case taskProperties: Map[String, Any] => taskProperties
    }.map(task => (task("host"), task("ports"))).map {
      case (extractIp(ip), List(port)) =>
        (ip, port.asInstanceOf[BigInt].toInt)
    }.map {
      case (ip, port) => Address("akka.tcp", systemName, ip, port)
    }
  }

}