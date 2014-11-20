akka-cluster-poc
================

```shell

  docker stop $(docker ps -a -q)
  docker rm $(docker ps -a -q)

  sbt docker
  
  zkCli.sh 
  => puis taper rmr /akka
  
  export KAFKA_HOME=/opt/kafka
  $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
  
  export KAFKA_HOME=/opt/kafka
  $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_seed
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_blacklist
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_gatewaybank
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_supervisor
  
```

TODO
====

Activates kafka
