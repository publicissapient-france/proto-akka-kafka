package fr.xebia.poc.core

import java.net.{InetSocketAddress, URL}

import akka.actor.{ActorSystem, Address, AddressFromURIString}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.framework.recipes.leader.LeaderLatch
import scala.collection.immutable
import org.apache.zookeeper.KeeperException.NodeExistsException

import scala.collection.convert.wrapAsScala._
import scala.collection.immutable
import scala.io.Source
import scala.util.{Success, Failure, Try}

object SeedDiscovery {

  private val extractIp = """ip-([\d-]*)\..*""".r


  def joinCluster(): Cluster = {

    println(
      s"""
         |
         |
         |Environment variables: ${System.getenv().mkString("\n")}
         |
         |
         |""".stripMargin)

    val system = ActorSystem.create("clustering-cluster", ConfigFactory.load("reference.conf"))

    val cluster = Cluster(system)


    val address = cluster.selfAddress

    val seedNodes = ClusteringEnvironment.seedNodes.map { seeds =>

      println( s"""Starting with seed-nodes from env variable $$SEED_NODES""")
      seeds.split(',').map(AddressFromURIString.apply).toList

    }.orElse {

      ClusteringEnvironment.marathonAddress.map { marathonAddress =>

        println(s"Discovering seed-nodes from marathon:  ($marathonAddress)")
        SeedDiscovery.seedNodesFromMarathon(system.name, marathonAddress.getHostName, marathonAddress.getPort)
      }
    }.getOrElse(immutable.Seq.empty)

    if (seedNodes.isEmpty) {
      println(
        """
          |
          |
          |First to join the cluster, no seed-nodes found
          |
          |
        """.stripMargin)

      cluster.joinSeedNodes(List(cluster.selfAddress))

    } else {
      println(
        s"""
           |
           |
           |Joining the cluster with seed-nodes [${
          seedNodes.mkString(", ")
        }]
           |
           |
           |""".stripMargin)
      cluster.joinSeedNodes(seedNodes)
    }

    cluster
  }

  private def seedNodesFromMarathon(systemName: String, marathonHost: String, marathonPort: Int): immutable.Seq[Address] = {
    val marathonWS = Try {
      Source.fromURL(new URL("http", marathonHost, marathonPort, "/v2/apps/akka-cluster-seed/tasks"))
    }

    val reply = marathonWS.map(_.getLines().toList.mkString(""))
    val tasks = reply.map(parse(_))

    val result = tasks.map(_.children.collect {
      case array: JArray => array
    }.flatMap(_.values).collect {
      case taskProperties: Map[String, Any] => taskProperties
    }.map(task => (task("host"), task("ports"))).map {
      case (extractIp(ip), List(port)) =>
        (ip.replaceAll("-", "\\."), port.asInstanceOf[BigInt].toInt)
    }.map {
      case (ip, port) => Address("akka.tcp", systemName, ip, port)
    })

    result match {
      case Success(addresses) =>
        addresses

      case Failure(e) =>
        println(s"Can't join marathon, ${e.getMessage}")
        immutable.Seq.empty
    }
  }

  private object ClusteringEnvironment {

    def seedNodes = Option(System.getenv("SEED_NODES"))

    def marathonAddress = (Option(System.getenv("MARATHON_IP")), Option(System.getenv("MARATHON_PORT"))) match {
      case (Some(host), Some(port)) => Some(new InetSocketAddress(host, port.toInt))
      case _ => None
    }
  }

}