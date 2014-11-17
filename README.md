akka-cluster-poc
================

```shell
  sbt docker
  
  export KAFKA_HOME=/opt/kafka
  $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
  $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_seed
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_tok
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_ano
  
```

TODO
====

Activates kafka
