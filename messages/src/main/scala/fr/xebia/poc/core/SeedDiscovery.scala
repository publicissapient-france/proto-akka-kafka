package fr.xebia.poc.core

import akka.actor.{ActorSystem, AddressFromURIString}
import akka.cluster.Cluster
import akka.cluster.seed.ZookeeperClusterSeed
import com.typesafe.config.ConfigFactory

import scala.collection.convert.wrapAsScala._

object SeedDiscovery {

  def joinCluster(): Cluster = {

    println(
      s"""
         |
         |
         |Environment variables: ${System.getenv().mkString("\n")}
         |
         |
         |""".stripMargin)

    val configuration = ConfigFactory.load("reference.conf")
    val system = ActorSystem.create("clustering-cluster", configuration)

    val cluster = Cluster(system)

    ClusteringEnvironment.seedNodes.map { seeds =>

      println( s"""Starting with seed-nodes from env variable $$SEED_NODES""")
      val seedNodes = seeds.split(',').map(AddressFromURIString.apply).toList

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

    }.getOrElse {

      println(
        s"""
           |
           |
           |Joining the cluster with seed-nodes from zookeeper ${configuration.getString("clustering.kafka-ip")}:${configuration.getString("clustering.kafka-port")}
           |
           |
           |""".stripMargin)

      ZookeeperClusterSeed(system).join()
    }

    cluster
  }

  private object ClusteringEnvironment {

    def seedNodes = Option(System.getenv("SEED_NODES"))

  }

}