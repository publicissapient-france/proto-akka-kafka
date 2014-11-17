akka-cluster-poc
================

```shell
  sbt docker
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_seed
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_tok
  
  docker run -e KAFKA_IP=$(hostname) -i -t xebiafrance/poc_cluster_akka_docker_ano
```

TODO
====

Activates kafka
