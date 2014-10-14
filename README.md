akka-cluster-poc
================

```shell
  sbt docker
  
  docker run -i -t --name seed xebia.fr/poc_cluster_akka_docker_seed
  
  docker run --name tokeniser1 --link seed:seed -i -t xebia.fr/poc_cluster_akka_docker_tokeniser
  
  docker run --name anonymiser1 --link seed:seed -i -t xebia.fr/poc_cluster_akka_docker_anonymiser
```

TODO
====

Activates kafka
